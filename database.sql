use gpapp


mongo  111.1.76.108/gpapp -u jack -p





db.createUser( { "user" : "jack",
                 "pwd": "1313",

                 "roles" : [ { role: "clusterAdmin", db: "admin" },
                             { role: "readAnyDatabase", db: "admin" },
                             "readWrite"
                             ] },
               { w: "majority" , wtimeout: 5000 } )


show dbs
show collections

--用户表

db.users.insert({
username : "dkr",
realname : "董康然",
usertype:"1",
password:"1"
})



--文章表

db.arctiles.insert({
content : "hello",
title:"",
type:"",
source:"",
time:new Date(),
titleimage:""
})


--消息表
db.messages.insert({
content : "hello",
fromid:"",
toid:"",
groupid:"",
time:new Date(),
isread:false,
ftype:'text',
mtype:"0"
})


--在线课堂表
db.onlineclass.insert({
title : "hello",
classtime:"",
place:"",
userid:"",
realname:"",
livepath:"",
time:new Date()

})


--学分课程表
db.studypoints.insert({
      title : "hello",
      videofile:"",
      points:"",

})

--用户学分表
db.userstudypoint.insert({
      studypointid : "hello",
      userid:"",
      timelearn:""


})

