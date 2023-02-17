FILE_NAME=$1
docker exec tl_ff bash -c "cd /tmp/ffplusplus_project/classification && bash evaluate.sh '/data/data1/datasets/'${FILE_NAME}" > adversarial_detection.temp 2>&1
cat adversarial_detection.temp > adversarial_detection.log
cat /dev/null > adversarial_detection.temp