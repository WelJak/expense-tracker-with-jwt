FROM postgres:11.5-alpine
COPY init-database.sql /docker-entrypoint-initdb.d/
COPY init-script.sh /docker-entrypoint-initdb.d/