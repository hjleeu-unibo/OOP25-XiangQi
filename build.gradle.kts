plugins { 
    java 
    application
}

application{
    mainClass.set("it.unibo.xiangqi.app.XiangqiApplication")
}

tasks.javadoc {
    options.memberLevel = JavadocMemberLevel.PUBLIC
}