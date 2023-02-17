docker exec forgery_detection_v3 /bin/bash -c 'cd /workspace/forgery && python demo_two_stream.py --files /workspace/data/workspace/forgery_detection' > forgery_detection.temp 2>&1
cat forgery_detection.temp > forgery_detection.log
cat /dev/null > forgery_detection.temp