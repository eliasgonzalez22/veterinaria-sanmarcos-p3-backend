#!/usr/bin/env bash
# ==========================================================
# Despliegue en la EC2 (Ubuntu) para la demo de la Parcial 3.
# Deja MySQL, el backend y el frontend como servicios que
# arrancan solos al encender la instancia. Se ejecuta una vez:
#
#   bash ~/veterinaria-sanmarcos-p3-backend/despliegue/instalar-servicios.sh
#
# Requisitos: Java 17, Maven, Docker, Node/npm y el archivo
# ~/sanmarcos-env.sh con DB_PASSWORD y JWT_SECRET (README).
# ==========================================================
set -euo pipefail

BACKEND_DIR="${BACKEND_DIR:-$HOME/veterinaria-sanmarcos-p3-backend}"
FRONTEND_DIR="${FRONTEND_DIR:-$HOME/veterinaria-sanmarcos-p2}"
ENV_SH="$HOME/sanmarcos-env.sh"
ENV_SERVICIO="/etc/veterinaria-sanmarcos.env"
USUARIO="$(whoami)"

echo "==> 1/5 Variables de entorno"
if [ ! -f "$ENV_SH" ]; then
  echo "Falta $ENV_SH (ver README: Cómo ejecutarlo)." >&2
  exit 1
fi
# shellcheck disable=SC1090
source "$ENV_SH"
: "${DB_PASSWORD:?DB_PASSWORD no está definido en $ENV_SH}"
: "${JWT_SECRET:?JWT_SECRET no está definido en $ENV_SH}"
# systemd no entiende "export": se escribe un archivo KEY=VALOR, legible solo por root.
sudo install -m 600 /dev/null "$ENV_SERVICIO"
sudo tee "$ENV_SERVICIO" > /dev/null <<EOF
DB_PASSWORD=${DB_PASSWORD}
JWT_SECRET=${JWT_SECRET}
EOF

echo "==> 2/5 MySQL en Docker (se reinicia solo con la instancia)"
sudo systemctl enable --now docker
(cd "$BACKEND_DIR" && sudo DB_PASSWORD="$DB_PASSWORD" docker compose up -d)

echo "==> 3/5 Compilando el backend (ms-usuarios)"
(cd "$BACKEND_DIR/ms-usuarios" && mvn -q -DskipTests package)
JAR="$(ls "$BACKEND_DIR"/ms-usuarios/target/ms-usuarios-*.jar | grep -v original | head -1)"
JAVA_BIN="$(command -v java)"

echo "==> 4/5 Compilando el frontend"
NPM_BIN="$(command -v npm)"
NODE_DIR="$(dirname "$(command -v node)")"
cd "$FRONTEND_DIR"
# La API se llama por ruta relativa y Vite la reenvía al backend (vite.config.js).
echo "VITE_API_URL=/api/v1" > .env
npm ci --no-audit --no-fund
npm run build

echo "==> 5/5 Servicios de systemd"
sudo tee /etc/systemd/system/vsm-backend.service > /dev/null <<EOF
[Unit]
Description=Veterinaria San Marcos - backend ms-usuarios (Spring Boot)
After=network-online.target docker.service
Wants=network-online.target

[Service]
User=${USUARIO}
WorkingDirectory=${BACKEND_DIR}/ms-usuarios
EnvironmentFile=${ENV_SERVICIO}
ExecStart=${JAVA_BIN} -jar ${JAR}
# Si MySQL todavía no está listo al encender, vuelve a intentar.
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

sudo tee /etc/systemd/system/vsm-frontend.service > /dev/null <<EOF
[Unit]
Description=Veterinaria San Marcos - frontend React (vite preview)
After=network-online.target vsm-backend.service

[Service]
User=${USUARIO}
WorkingDirectory=${FRONTEND_DIR}
Environment=PATH=${NODE_DIR}:/usr/local/bin:/usr/bin:/bin
ExecStart=${NPM_BIN} start
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable vsm-backend vsm-frontend
sudo systemctl restart vsm-backend vsm-frontend

echo
echo "Listo. Esperando a que el backend responda..."
for _ in $(seq 1 30); do
  if curl -s -o /dev/null http://localhost:8081/v3/api-docs; then break; fi
  sleep 3
done
bash "$BACKEND_DIR/despliegue/ver-direcciones.sh"
