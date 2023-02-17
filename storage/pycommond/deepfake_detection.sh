docker exec df_det_sys /bin/bash -c 'cd /df_system && python3 detect_from_video.py -i /out_df/workspace/deepfake_detection/ -o /out_df/result/deepfake_detection' > deepfake_detection.temp 2>&1
cat deepfake_detection.temp > deepfake_detection.log
cat /dev/null > deepfake_detection.temp