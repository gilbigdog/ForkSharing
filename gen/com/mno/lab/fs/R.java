/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * aapt tool from the resource data it found.  It
 * should not be modified by hand.
 */

package com.mno.lab.fs;

public final class R {
    public static final class attr {
        /**  Defines panel animation duration in ms. 
         <p>Must be an integer value, such as "<code>100</code>".
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
         */
        public static final int animationDuration=0x7f010000;
        /**  Defines closed handle (drawable/color). 
         <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static final int closedHandle=0x7f010007;
        /**  Identifier for the child that represents the panel's content. 
         <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
         */
        public static final int content=0x7f010003;
        /**  Identifier for the child that represents the panel's handle. 
         <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
         */
        public static final int handle=0x7f010002;
        /**  Defines if flying gesture forces linear interpolator in animation. 
         <p>Must be a boolean value, either "<code>true</code>" or "<code>false</code>".
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
         */
        public static final int linearFlying=0x7f010004;
        /**  Defines opened handle (drawable/color). 
         <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static final int openedHandle=0x7f010006;
        /**  Defines panel position on the screen. 
         <p>Must be one of the following constant values.</p>
<table>
<colgroup align="left" />
<colgroup align="left" />
<colgroup align="left" />
<tr><th>Constant</th><th>Value</th><th>Description</th></tr>
<tr><td><code>top</code></td><td>0</td><td> Panel placed at top of the screen. </td></tr>
<tr><td><code>bottom</code></td><td>1</td><td> Panel placed at bottom of the screen. </td></tr>
<tr><td><code>left</code></td><td>2</td><td> Panel placed at left of the screen. </td></tr>
<tr><td><code>right</code></td><td>3</td><td> Panel placed at right of the screen. </td></tr>
</table>
         */
        public static final int position=0x7f010001;
        /**  Defines size relative to parent (must be in form: nn%p). 
         <p>Must be a fractional value, which is a floating point number appended with either % or %p, such as "<code>14.5%</code>".
The % suffix always means a percentage of the base size; the optional %p suffix provides a size relative to
some parent container.
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
         */
        public static final int weight=0x7f010005;
    }
    public static final class dimen {
        public static final int drawer_handle_thick=0x7f070000;
    }
    public static final class drawable {
        public static final int ic_contact_picture=0x7f020000;
        public static final int ic_launcher=0x7f020001;
        public static final int ic_launcher_gallery=0x7f020002;
        public static final int ic_launcher_video=0x7f020003;
    }
    public static final class id {
        public static final int app_icon=0x7f060005;
        public static final int app_name_view=0x7f060006;
        public static final int bottom=0x7f060001;
        public static final int contact_name=0x7f060009;
        public static final int contact_phonenumber=0x7f060008;
        public static final int contact_pic=0x7f060007;
        public static final int dialog_app_share_apk_view=0x7f06000a;
        public static final int dialog_app_share_info_view=0x7f06000b;
        public static final int left=0x7f060002;
        public static final int right=0x7f060003;
        public static final int top=0x7f060000;
        public static final int tv_nfc=0x7f060004;
    }
    public static final class layout {
        public static final int activity_nfc=0x7f030000;
        public static final int application_item=0x7f030001;
        public static final int contact_item=0x7f030002;
        public static final int dialog_app_share=0x7f030003;
    }
    public static final class string {
        public static final int app_name=0x7f040000;
        /**  Contact Layout Description 
         */
        public static final int contact_pic_string=0x7f040014;
        public static final int contents_contact=0x7f040002;
        public static final int contents_email=0x7f040003;
        public static final int contents_location=0x7f040004;
        public static final int contents_phone=0x7f040005;
        public static final int contents_sms=0x7f040006;
        public static final int contents_text=0x7f040007;
        public static final int current_channel_tv=0x7f04000d;
        /**  Application Share Dialog 
         */
        public static final int dialog_app_share_apk_title=0x7f040015;
        public static final int dialog_app_share_info_title=0x7f040016;
        public static final int join_channel_button_string=0x7f04000c;
        public static final int magnet_service_action=0x7f04000e;
        public static final int manual_string=0x7f04000b;
        public static final int menu_settings=0x7f040009;
        public static final int msg_encode_contents_failed=0x7f040008;
        public static final int nfc_string=0x7f04000a;
        public static final int nv_nfc_string=0x7f040011;
        public static final int nv_no_nfc_capable_string=0x7f040010;
        /**  NFC View Description 
         */
        public static final int nv_no_nfc_string=0x7f04000f;
        public static final int nv_no_wifi_no_nfc_string=0x7f040012;
        public static final int nv_no_wifi_string=0x7f040013;
        public static final int service_action=0x7f040001;
    }
    public static final class style {
        /** 
        Base application theme, dependent on API level. This theme is replaced
        by AppBaseTheme from res/values-vXX/styles.xml on newer devices.
    

            Theme customizations available in newer API levels can go in
            res/values-vXX/styles.xml, while customizations related to
            backward-compatibility can go here.
        

        Base application theme for API 11+. This theme completely replaces
        AppBaseTheme from res/values/styles.xml on API 11+ devices.
    
 API 11 theme customizations can go here. 

        Base application theme for API 14+. This theme completely replaces
        AppBaseTheme from BOTH res/values/styles.xml and
        res/values-v11/styles.xml on API 14+ devices.
    
 API 14 theme customizations can go here. 

        Base application theme, dependent on API level. This theme is replaced
        by AppBaseTheme from res/values-vXX/styles.xml on newer devices.
    

            Theme customizations available in newer API levels can go in
            res/values-vXX/styles.xml, while customizations related to
            backward-compatibility can go here.
        

        Base application theme for API 11+. This theme completely replaces
        AppBaseTheme from res/values/styles.xml on API 11+ devices.
    
 API 11 theme customizations can go here. 

        Base application theme for API 14+. This theme completely replaces
        AppBaseTheme from BOTH res/values/styles.xml and
        res/values-v11/styles.xml on API 14+ devices.
    
 API 14 theme customizations can go here. 
         */
        public static final int AppBaseTheme=0x7f050000;
        /**  Application theme. 
 All customizations that are NOT specific to a particular API-level can go here. 
 Application theme. 
 All customizations that are NOT specific to a particular API-level can go here. 
         */
        public static final int AppTheme=0x7f050001;
    }
    public static final class styleable {
        /** Attributes that can be used with a Panel.
           <p>Includes the following attributes:</p>
           <table>
           <colgroup align="left" />
           <colgroup align="left" />
           <tr><th>Attribute</th><th>Description</th></tr>
           <tr><td><code>{@link #Panel_animationDuration com.mno.lab.fs:animationDuration}</code></td><td> Defines panel animation duration in ms.</td></tr>
           <tr><td><code>{@link #Panel_closedHandle com.mno.lab.fs:closedHandle}</code></td><td> Defines closed handle (drawable/color).</td></tr>
           <tr><td><code>{@link #Panel_content com.mno.lab.fs:content}</code></td><td> Identifier for the child that represents the panel's content.</td></tr>
           <tr><td><code>{@link #Panel_handle com.mno.lab.fs:handle}</code></td><td> Identifier for the child that represents the panel's handle.</td></tr>
           <tr><td><code>{@link #Panel_linearFlying com.mno.lab.fs:linearFlying}</code></td><td> Defines if flying gesture forces linear interpolator in animation.</td></tr>
           <tr><td><code>{@link #Panel_openedHandle com.mno.lab.fs:openedHandle}</code></td><td> Defines opened handle (drawable/color).</td></tr>
           <tr><td><code>{@link #Panel_position com.mno.lab.fs:position}</code></td><td> Defines panel position on the screen.</td></tr>
           <tr><td><code>{@link #Panel_weight com.mno.lab.fs:weight}</code></td><td> Defines size relative to parent (must be in form: nn%p).</td></tr>
           </table>
           @see #Panel_animationDuration
           @see #Panel_closedHandle
           @see #Panel_content
           @see #Panel_handle
           @see #Panel_linearFlying
           @see #Panel_openedHandle
           @see #Panel_position
           @see #Panel_weight
         */
        public static final int[] Panel = {
            0x7f010000, 0x7f010001, 0x7f010002, 0x7f010003,
            0x7f010004, 0x7f010005, 0x7f010006, 0x7f010007
        };
        /**
          <p>
          @attr description
           Defines panel animation duration in ms. 


          <p>Must be an integer value, such as "<code>100</code>".
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
          <p>This is a private symbol.
          @attr name android:animationDuration
        */
        public static final int Panel_animationDuration = 0;
        /**
          <p>
          @attr description
           Defines closed handle (drawable/color). 


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          <p>This is a private symbol.
          @attr name android:closedHandle
        */
        public static final int Panel_closedHandle = 7;
        /**
          <p>
          @attr description
           Identifier for the child that represents the panel's content. 


          <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
          <p>This is a private symbol.
          @attr name android:content
        */
        public static final int Panel_content = 3;
        /**
          <p>
          @attr description
           Identifier for the child that represents the panel's handle. 


          <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
          <p>This is a private symbol.
          @attr name android:handle
        */
        public static final int Panel_handle = 2;
        /**
          <p>
          @attr description
           Defines if flying gesture forces linear interpolator in animation. 


          <p>Must be a boolean value, either "<code>true</code>" or "<code>false</code>".
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
          <p>This is a private symbol.
          @attr name android:linearFlying
        */
        public static final int Panel_linearFlying = 4;
        /**
          <p>
          @attr description
           Defines opened handle (drawable/color). 


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          <p>This is a private symbol.
          @attr name android:openedHandle
        */
        public static final int Panel_openedHandle = 6;
        /**
          <p>
          @attr description
           Defines panel position on the screen. 


          <p>Must be one of the following constant values.</p>
<table>
<colgroup align="left" />
<colgroup align="left" />
<colgroup align="left" />
<tr><th>Constant</th><th>Value</th><th>Description</th></tr>
<tr><td><code>top</code></td><td>0</td><td> Panel placed at top of the screen. </td></tr>
<tr><td><code>bottom</code></td><td>1</td><td> Panel placed at bottom of the screen. </td></tr>
<tr><td><code>left</code></td><td>2</td><td> Panel placed at left of the screen. </td></tr>
<tr><td><code>right</code></td><td>3</td><td> Panel placed at right of the screen. </td></tr>
</table>
          <p>This is a private symbol.
          @attr name android:position
        */
        public static final int Panel_position = 1;
        /**
          <p>
          @attr description
           Defines size relative to parent (must be in form: nn%p). 


          <p>Must be a fractional value, which is a floating point number appended with either % or %p, such as "<code>14.5%</code>".
The % suffix always means a percentage of the base size; the optional %p suffix provides a size relative to
some parent container.
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
          <p>This is a private symbol.
          @attr name android:weight
        */
        public static final int Panel_weight = 5;
    };
}
