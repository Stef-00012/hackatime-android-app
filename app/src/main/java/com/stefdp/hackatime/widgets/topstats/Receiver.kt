package com.stefdp.hackatime.widgets.topstats

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class Receiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TopStatsWidget()
}