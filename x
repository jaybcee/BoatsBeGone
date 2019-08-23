[Unit]
Description=Gunicorn instance to serve myproject
After=network.target

[Service]
User=ubuntu
Group=www-data
WorkingDirectory=/home/ubuntu/BoatsBeGone
Environment="PATH=/home/ubuntu/BoatsBeGone/myprojectenv/bin"
ExecStart=/home/ubuntu/BoatsBeGone/myprojectenv/bin/gunicorn --workers 3 --bind unix:myproject.sock -m 007 wsgi:app

[Install]
WantedBy=multi-user.target
