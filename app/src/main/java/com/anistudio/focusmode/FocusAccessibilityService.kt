package com.anistudio.focusmode
import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
class FocusAccessibilityService : AccessibilityService() {
 override fun onAccessibilityEvent(event: AccessibilityEvent?) {
  if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && event?.eventType != AccessibilityEvent.TYPE_WINDOWS_CHANGED) return
  val p=getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE); val end=p.getLong(MainActivity.KEY_END,0L)
  if(end==0L || System.currentTimeMillis()>=end) return
  val pkg=event.packageName?.toString() ?: return
  if(pkg==packageName || pkg=="com.android.systemui") return
  val blocked=p.getStringSet(MainActivity.KEY_BLOCKED, emptySet()) ?: emptySet()
  if(pkg in blocked) startActivity(Intent(this,BlockActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
 }
 override fun onInterrupt() {}
}
