package com.example.screentime

class ScreenTimeEntry {
    var answered: Boolean = false
    var date: String = ""
    var evaluated: Boolean = false
    var productiveTime: String = ""
    var score: String = ""
    var timeSpend: String = ""
    var qAProductiveTime: String = ""
    var qAScore: String = ""
    var qATimeSpend: String = ""

    constructor(
        answered: Boolean,
        date: String,
        evaluated: Boolean,
        productiveTime: String,
        score: String,
        timeSpend: String,
        qAProductiveTime: String,
        qAScore: String,
        qATimeSpend: String
    ) {
        this.answered = answered
        this.date = date
        this.evaluated = evaluated
        this.productiveTime = productiveTime
        this.score = score
        this.timeSpend = timeSpend
        this.qAProductiveTime = qAProductiveTime
        this.qAScore = qAScore
        this.qATimeSpend = qATimeSpend

    }

    constructor(
        answered: Boolean,
        date: String,
        evaluated: Boolean,
        productiveTime: String,
        score: String,
        timeSpend: String
    ) {
        this.answered = answered
        this.date = date
        this.evaluated = evaluated
        this.productiveTime = productiveTime
        this.score = score
        this.timeSpend = timeSpend
    }
}