from crontab import CronTab

cron = CronTab(user=True)

cron_day_dict = {
    "monday": "MON",
    "tuesday": "TUE",
    "wednesday": "WED",
    "thursday": "THU",
    "friday": "FRI",
    "saturday": "SAT",
    "sunday": "SUN"
}

def check_existing(id,day,time):
    did_find = False
    for job in cron:
        if job.comment == f"{id} {day} {time.hour} {time.minute}":
            did_find= True
    return did_find

def remove(id):
    for job in cron:
        if id in job.comment:
            cron.remove(job)
            cron.write()
    return True


def add_job(id,day,time):
    job = cron.new(command=f'/home/ubuntu/BoatsBeGone/rem.py {id}', comment=f"{id} {day} {time.hour} {time.minute}")
    job.dow.on(cron_day_dict[day])
    job.hour.on(time.hour)
    job.minute.on(time.minute)
    cron.write()