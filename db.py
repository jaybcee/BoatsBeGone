from secrets import *
from pymongo import MongoClient
import dns

# authenticate into mongo client
client = MongoClient(MONGO)

# chose database from cluster
db = client.get_database('boat_crossing')


# select the "document"
#       db
#    /  |   \
# misc  up  down
def add_entry(type, start, end, timeElapsed):

    if (type == 'upstream'):
        pick_db = db.upstream
    elif (type == 'downstream'):
        pick_db = db.downstream
    else:
        pick_db = db.misc

    crossing_ = {
        'start': f"{start}",
        'end': f"{end}",
        'timeElapsed': f"{timeElapsed}"
    }

    pick_db.insert_one(crossing_)

def add_fb(txt,id):
    smtn ={
        'text': f"{txt}",
        'id':f"{id}"
    }
    db.fb.insert_one(smtn)
