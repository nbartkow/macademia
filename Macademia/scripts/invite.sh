if ! [ -f "$1" ]; then
    echo "usage: $0 emails_file email_template" >& 2
    exit 1
fi
if [ -z "$2" ]; then
    echo "usage: $0 emails_file email_template" >& 2
    exit 1
fi

EMAIL_FILE=$1
EMAIL_TEMPLATE=$2
ADMIN_URL=http://localhost:8080/Macademia/all/administrator

wget -O - "$ADMIN_URL/resetKey"

echo -n "enter new auth key from log file: "
read key

while read email; do
    echo "inviting $email"
    wget -O - "$ADMIN_URL/invite?key=$key&email=$email&template=$EMAIL_TEMPLATE"
    echo
    echo
done <$EMAIL_FILE
