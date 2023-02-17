docker exec df_det_sys /bin/bash -c 'cd /df_system && python3 detect_from_image.py -i /out_df/image -o /out_df/result/deepfake_detection_image' > deepfake_detection_image.temp 2>&1
cat deepfake_detection_image.temp > deepfake_detection_image.log
cat /dev/null > deepfake_detection_image.temp