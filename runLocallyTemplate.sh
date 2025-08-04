docker build --tag 'dietibackend' .

docker run -p 8080:8080 \
    -e DB_URL='jdbc:postgresql://dieti-estates.postgres.database.azure.com:5432/dieti_estates' \
    -e DB_USERNAME='your_username' \
    -e DB_PASSWORD='your_password' \
    dietibackend