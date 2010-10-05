while read email; do
    echo "inviting $email"
    wget -O - "http://localhost:10090/Macademia/person/reinvite?email=$email"
    echo
    echo
done
