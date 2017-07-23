git clone https://github.com/rigocalin/worshipsongs-db-dev bundle-db
echo "Copy file into assest dir"
cp -rf bundle-db/songs.sqlite app/src/main/assets/
ls -l app/src/main/assets
rm -rf bundle-db
