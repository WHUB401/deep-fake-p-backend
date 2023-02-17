docker exec trace_detection /bin/bash -c 'cd /workspace && python pred_model.py' > trace_detection.temp 2>&1
cat trace_detection.temp > trace_detection.log
cat /dev/null > trace_detection.temp