app_jars=( portfolio-site-*.jar )
echo "Original app file: ${app_jars[0]}" > original_file.txt
mv "${app_jars[0]}" app.jar
