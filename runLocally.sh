#!/bin/bash
docker build --tag 'dietibackend' .
# docker build --no-cache --tag 'dietibackend' .

docker run --rm -p 8080:8080 \
    -v "$(pwd)/logs":/app/logs \
    -e DB_URL="jdbc:postgresql://dieti-estates.postgres.database.azure.com:5432/dieti_estates" \
    -e DB_USERNAME="lucabarrella" \
    -e DB_PASSWORD="gexsaq-6sichI-nehhit" \
    -e ACCESS_TOKEN_SECRET_KEY="2dcab0830cd4dcf26ad2c9e391b9506374115ea23003577d725dd84af215394ba484115edd1a9a6d353c32a93639c504dec6129e8d7c08199cc93a176d02c8a93d7c8b2fefc19ba635d21dda019d495d7306401abf4d376a54922a9d5e5d9d93556a3282c4d10b44298c3b5f9882b66dbc160fd601e2744b9904cb004576bdb9916e54dbb5f4bffb994a684315c8460879cad631a5298a13814461d1863bf6620fdb48b173e16705b16d5eee9626dd9743ed91e0095927fde51cf2128ecadf3df9dd9ebc62f347ac11c9ba8edfdebd3bcce1238be50f96e6af496c82978e17ea00bfbc2a26ea16b9aa87e5107788081e00b078522147668f537305304de201f1" \
    -e AZURE_STORAGE_CONNECTION_STRING="DefaultEndpointsProtocol=https;AccountName=storagedietiestates25;AccountKey=oB2GIkwTE8ZBoirdfnCru2VE/DZTKc4Wf+UMoPsXCEKHpKDDHtElBb8kLe9zQwIuSuIPgt3ieniv+AStcNC4Lg==;EndpointSuffix=core.windows.net" \
    -e AZURE_CONTAINER_NAME="immagini-dietiestates25" \
    -e GEOAPIFY_API_KEY="YOUR_KEY" \
    dietibackend