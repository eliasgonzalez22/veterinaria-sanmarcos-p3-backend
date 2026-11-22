#!/usr/bin/env bash
# Muestra el estado de los servicios y las direcciones públicas para la demo.
# La IP pública cambia cada vez que se inicia el laboratorio de AWS Academy.
#   bash ~/veterinaria-sanmarcos-p3-backend/despliegue/ver-direcciones.sh

TOKEN="$(curl -s -X PUT http://169.254.169.254/latest/api/token \
  -H 'X-aws-ec2-metadata-token-ttl-seconds: 60')"
IP="$(curl -s -H "X-aws-ec2-metadata-token: $TOKEN" \
  http://169.254.169.254/latest/meta-data/public-ipv4)"

estado() {
  if systemctl is-active --quiet "$1"; then echo "activo"; else echo "DETENIDO"; fi
}

if curl -s -o /dev/null -w '%{http_code}' http://localhost:8081/v3/api-docs | grep -q 200; then
  API="responde"
else
  API="NO responde (revisa: sudo journalctl -u vsm-backend -n 50)"
fi

echo "MySQL (Docker):  $(sudo docker inspect -f '{{.State.Status}}' sanmarcos-mysql-usuarios 2>/dev/null || echo 'no existe')"
echo "Backend:         $(estado vsm-backend) · API $API"
echo "Frontend:        $(estado vsm-frontend)"
echo
echo "Frontend:  http://${IP}:5173"
echo "Swagger:   http://${IP}:8081/swagger-ui.html"
echo
echo "Grupo de seguridad: deben estar abiertos los puertos 5173 y 8081 (TCP, 0.0.0.0/0)."
