package com.anistudio.focusmode
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.TimeUnit

class MainActivity : Activity() {
 companion object { const val PREFS="focus_mode_prefs"; const val KEY_END="session_end"; const val KEY_BLOCKED="blocked_packages"; const val KEY_TOTAL="focus_minutes_total" }
 private val bg=Color.rgb(8,10,20); private val panel=Color.rgb(20,23,42); private val accent=Color.rgb(124,110,255)
 private val prefs by lazy { getSharedPreferences(PREFS,MODE_PRIVATE) }
 private var duration=25; private var timer:TextView?=null; private val handler=Handler(Looper.getMainLooper())
 private val tick=object:Runnable { override fun run(){ updateTimer(); handler.postDelayed(this,1000) } }
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);window.statusBarColor=bg;window.navigationBarColor=bg;showHome()}
 override fun onResume(){super.onResume();updateTimer()}
 override fun onDestroy(){handler.removeCallbacks(tick);super.onDestroy()}
 private fun base()=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(22,22,22,22);setBackgroundColor(bg)}
 private fun label(t:String,s:Float,c:Int=Color.WHITE,b:Boolean=false)=TextView(this).apply{text=t;textSize=s;setTextColor(c);if(b)typeface=Typeface.DEFAULT_BOLD;setPadding(0,7,0,7)}
 private fun button(t:String,action:()->Unit)=Button(this).apply{text=t;isAllCaps=false;textSize=15f;setOnClickListener{action()}}
 private fun card()=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(16,12,16,12);setBackgroundColor(panel)}
 private fun showHome(){
  val root=base();root.addView(label("ANI STUDIO  /  WELLBEING",12f,accent,true));root.addView(label("FOCUS MODE",32f,Color.WHITE,true));root.addView(label("Protect your attention. Build your future.",15f,Color.LTGRAY))
  val scroll=ScrollView(this);val content=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  val session=card();session.addView(label("YOUR FOCUS SESSION",12f,accent,true));timer=label("25:00",52f,Color.WHITE,true).also{it.gravity=Gravity.CENTER};session.addView(timer);session.addView(label("Choose your session length",14f,Color.LTGRAY))
  val choices=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER}
  listOf(25,45,60).forEach{m->choices.addView(Button(this).apply{text="$m min";isAllCaps=false;setOnClickListener{duration=m;if(prefs.getLong(KEY_END,0L)<=System.currentTimeMillis())timer?.text=String.format("%02d:00",m)}},LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f))}
  session.addView(choices);session.addView(button("START FOCUS SESSION"){startSession()});session.addView(button("END SESSION"){endSession()});content.addView(session)
  val stats=card();stats.addView(label("YOUR PROGRESS",12f,accent,true));stats.addView(label("${prefs.getInt(KEY_TOTAL,0)} focus minutes recorded",21f,Color.WHITE,true));stats.addView(label("Small sessions. Strong habits.",14f,Color.LTGRAY));content.addView(stats)
  val shield=card();shield.addView(label("DISTRACTION SHIELD",12f,accent,true));shield.addView(label("Choose apps to redirect during your focus session.",14f,Color.LTGRAY));shield.addView(button("CHOOSE DISTRACTING APPS"){showAppPicker()});shield.addView(button("ENABLE BLOCKING SERVICE"){startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));Toast.makeText(this,"Find FOCUS MODE and enable its service",Toast.LENGTH_LONG).show()});content.addView(shield)
  content.addView(label("App blocking requires you to enable the accessibility service in Android Settings. You can disable it any time.",12f,Color.GRAY));scroll.addView(content);root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f));setContentView(root);handler.removeCallbacks(tick);handler.post(tick)
 }
 private fun startSession(){prefs.edit().putLong(KEY_END,System.currentTimeMillis()+duration*60000L).apply();Toast.makeText(this,"Focus session started",Toast.LENGTH_SHORT).show();updateTimer()}
 private fun endSession(){val end=prefs.getLong(KEY_END,0L);if(end>System.currentTimeMillis()){val elapsed=((duration*60000L-(end-System.currentTimeMillis())).coerceAtLeast(0L)/60000L).toInt();if(elapsed>0)prefs.edit().putInt(KEY_TOTAL,prefs.getInt(KEY_TOTAL,0)+elapsed).apply()};prefs.edit().putLong(KEY_END,0L).apply();timer?.text=String.format("%02d:00",duration);Toast.makeText(this,"Session ended",Toast.LENGTH_SHORT).show()}
 private fun updateTimer(){val end=prefs.getLong(KEY_END,0L);val remaining=(end-System.currentTimeMillis()).coerceAtLeast(0L);if(end>0&&remaining==0L){prefs.edit().putLong(KEY_END,0L).putInt(KEY_TOTAL,prefs.getInt(KEY_TOTAL,0)+duration).apply();Toast.makeText(this,"Session complete! Great work.",Toast.LENGTH_LONG).show()};val ms=if(remaining>0)remaining else duration*60000L;timer?.text=String.format("%02d:%02d",TimeUnit.MILLISECONDS.toMinutes(ms),TimeUnit.MILLISECONDS.toSeconds(ms)%60)}
 private fun showAppPicker(){
  val root=base();root.addView(label("YOUR DISTRACTION LIST",24f,Color.WHITE,true));root.addView(label("Select apps to redirect while your focus session is active.",14f,Color.LTGRAY))
  val scroll=ScrollView(this);val list=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};val selected=(prefs.getStringSet(KEY_BLOCKED,emptySet())?:emptySet()).toMutableSet()
  val apps=packageManager.getInstalledApplications(0).filter{it.packageName!=packageName&&packageManager.getLaunchIntentForPackage(it.packageName)!=null}.sortedBy{packageManager.getApplicationLabel(it).toString().lowercase()}
  apps.forEach{app->list.addView(CheckBox(this).apply{text=packageManager.getApplicationLabel(app).toString();setTextColor(Color.WHITE);isChecked=app.packageName in selected;setPadding(8,8,8,8);setOnCheckedChangeListener{_,checked->if(checked)selected.add(app.packageName)else selected.remove(app.packageName)}})}
  scroll.addView(list);root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f));root.addView(button("SAVE SELECTED APPS"){prefs.edit().putStringSet(KEY_BLOCKED,selected.toSet()).apply();Toast.makeText(this,"Saved ${selected.size} apps",Toast.LENGTH_SHORT).show();showHome()});root.addView(button("CANCEL"){showHome()});setContentView(root)
 }
}