package com.example.screentime

class Participant {
    var deviceId: String = ""
    var group: String = ""
    var qOne: Boolean = false
    var qThree: Boolean = false
    var qTwo: Boolean = false
    var screenTimeEntries: List<ScreenTimeEntry> = emptyList()
    var startDate: String = ""
    var studyID: Int = -1

    constructor(deviceId: String,
                group: String,
                qOne: Boolean,
                qThree: Boolean,
                qTwo: Boolean,
                screenTimeEntries: List<ScreenTimeEntry>,
                startDate: String,
                studyID: Int){
        this.deviceId = deviceId
        this.group = group
        this.qOne = qOne
        this.qThree = qThree
        this.qTwo = qTwo
        this.screenTimeEntries = screenTimeEntries
        this.startDate = startDate
        this.studyID = studyID
    }
}