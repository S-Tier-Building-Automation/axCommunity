package org.axcommunity.niagara.conversion;

import javax.baja.status.BIStatusValue;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusString;
import javax.baja.status.BStatusValue;
import javax.baja.sys.Action;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BValue;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Slot;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;


/**
 * 
 * Changes the following codes to their ASCII values:<br><br>
 * 
 * <blockquote>
 * <table border='1' bordercolor='#505050' cellspacing='0'>
 * <tr>
 * 	<th bgcolor='#666699' align=center><font color='#ffffff'>Symbol</font></th>
 * 	<th bgcolor='#666699' align=center><font color='#ffffff'>HTML<br>Number</font></th>
 * 	<th bgcolor='#666699' align=center><font color='#ffffff'>HTML<br>Name</font></th>
 * </tr>
 
 * <tr><td align=center><code></code></td><td align=center><code>&amp;#32;</code></td><td align=center><code></code></td></tr>

 * <tr><td align=center><code>!</code></td><td align=center><code>&amp;#33;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>"</code></td><td align=center><code>&amp;#34;</code></td><td align=center><code>&amp;quot;</code></td></tr>
 * <tr><td align=center><code>#</code></td><td align=center><code>&amp;#35;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>$</code></td><td align=center><code>&amp;#36;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>%</code></td><td align=center><code>&amp;#37;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>&</code></td><td align=center><code>&amp;#38;</code></td><td align=center><code>&amp;amp;</code></td></tr>
 * <tr><td align=center><code>'</code></td><td align=center><code>&amp;#39;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>(</code></td><td align=center><code>&amp;#40;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>)</code></td><td align=center><code>&amp;#41;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>*</code></td><td align=center><code>&amp;#42;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>+</code></td><td align=center><code>&amp;#43;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>,</code></td><td align=center><code>&amp;#44;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>-</code></td><td align=center><code>&amp;#45;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>.</code></td><td align=center><code>&amp;#46;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>/</code></td><td align=center><code>&amp;#47;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>0</code></td><td align=center><code>&amp;#48;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>1</code></td><td align=center><code>&amp;#49;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>2</code></td><td align=center><code>&amp;#50;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>3</code></td><td align=center><code>&amp;#51;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>4</code></td><td align=center><code>&amp;#52;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>5</code></td><td align=center><code>&amp;#53;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>6</code></td><td align=center><code>&amp;#54;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>7</code></td><td align=center><code>&amp;#55;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>8</code></td><td align=center><code>&amp;#56;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>9</code></td><td align=center><code>&amp;#57;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>:</code></td><td align=center><code>&amp;#58;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>;</code></td><td align=center><code>&amp;#59;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code><</code></td><td align=center><code>&amp;#60;</code></td><td align=center><code>&amp;lt;</code></td></tr>
 * <tr><td align=center><code>=</code></td><td align=center><code>&amp;#61;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>></code></td><td align=center><code>&amp;#62;</code></td><td align=center><code>&amp;gt;</code></td></tr>
 * <tr><td align=center><code>?</code></td><td align=center><code>&amp;#63;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>@</code></td><td align=center><code>&amp;#64;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>A</code></td><td align=center><code>&amp;#65;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>B</code></td><td align=center><code>&amp;#66;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>C</code></td><td align=center><code>&amp;#67;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>D</code></td><td align=center><code>&amp;#68;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>E</code></td><td align=center><code>&amp;#69;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>F</code></td><td align=center><code>&amp;#70;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>G</code></td><td align=center><code>&amp;#71;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>H</code></td><td align=center><code>&amp;#72;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>I</code></td><td align=center><code>&amp;#73;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>J</code></td><td align=center><code>&amp;#74;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>K</code></td><td align=center><code>&amp;#75;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>L</code></td><td align=center><code>&amp;#76;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>M</code></td><td align=center><code>&amp;#77;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>N</code></td><td align=center><code>&amp;#78;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>O</code></td><td align=center><code>&amp;#79;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>P</code></td><td align=center><code>&amp;#80;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>Q</code></td><td align=center><code>&amp;#81;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>R</code></td><td align=center><code>&amp;#82;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>S</code></td><td align=center><code>&amp;#83;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>T</code></td><td align=center><code>&amp;#84;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>U</code></td><td align=center><code>&amp;#85;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>V</code></td><td align=center><code>&amp;#86;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>W</code></td><td align=center><code>&amp;#87;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>X</code></td><td align=center><code>&amp;#88;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>Y</code></td><td align=center><code>&amp;#89;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>Z</code></td><td align=center><code>&amp;#90;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>[</code></td><td align=center><code>&amp;#91;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>\</code></td><td align=center><code>&amp;#92;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>]</code></td><td align=center><code>&amp;#93;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>^</code></td><td align=center><code>&amp;#94;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>_</code></td><td align=center><code>&amp;#95;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>`</code></td><td align=center><code>&amp;#96;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>a</code></td><td align=center><code>&amp;#97;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>b</code></td><td align=center><code>&amp;#98;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>c</code></td><td align=center><code>&amp;#99;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>d</code></td><td align=center><code>&amp;#100;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>e</code></td><td align=center><code>&amp;#101;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>f</code></td><td align=center><code>&amp;#102;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>g</code></td><td align=center><code>&amp;#103;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>h</code></td><td align=center><code>&amp;#104;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>i</code></td><td align=center><code>&amp;#105;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>j</code></td><td align=center><code>&amp;#106;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>k</code></td><td align=center><code>&amp;#107;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>l</code></td><td align=center><code>&amp;#108;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>m</code></td><td align=center><code>&amp;#109;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>n</code></td><td align=center><code>&amp;#110;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>o</code></td><td align=center><code>&amp;#111;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>p</code></td><td align=center><code>&amp;#112;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>q</code></td><td align=center><code>&amp;#113;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>r</code></td><td align=center><code>&amp;#114;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>s</code></td><td align=center><code>&amp;#115;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>t</code></td><td align=center><code>&amp;#116;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>u</code></td><td align=center><code>&amp;#117;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>v</code></td><td align=center><code>&amp;#118;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>w</code></td><td align=center><code>&amp;#119;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>x</code></td><td align=center><code>&amp;#120;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>y</code></td><td align=center><code>&amp;#121;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>z</code></td><td align=center><code>&amp;#122;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>{</code></td><td align=center><code>&amp;#123;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>|</code></td><td align=center><code>&amp;#124;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>}</code></td><td align=center><code>&amp;#125;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>~</code></td><td align=center><code>&amp;#126;</code></td><td align=center><code></code></td></tr>
 
 * <tr><td align=center><code></code></td><td align=center><code>&amp;#127;</code></td><td align=center><code></code></td></tr>
 
 
 * <tr><td align=center><code>&euro;</code></td><td align=center><code>&amp;#128;</code></td><td align=center><code>&amp;euro;</code></td></tr>
 * <tr><td align=center><code>&sbquo;</code></td><td align=center><code>&amp;#130;</code></td><td align=center><code>&amp;sbquo;</code></td></tr>
 * <tr><td align=center><code>&fnof;</code></td><td align=center><code>&amp;#131;</code></td><td align=center><code>&amp;fnof;</code></td></tr>
 * <tr><td align=center><code>&bdquo;</code></td><td align=center><code>&amp;#132;</code></td><td align=center><code>&amp;bdquo;</code></td></tr>
 * <tr><td align=center><code>&hellip;</code></td><td align=center><code>&amp;#133;</code></td><td align=center><code>&amp;hellip;</code></td></tr>
 * <tr><td align=center><code>&dagger;</code></td><td align=center><code>&amp;#134;</code></td><td align=center><code>&amp;dagger;</code></td></tr>
 * <tr><td align=center><code>&Dagger;</code></td><td align=center><code>&amp;#135;</code></td><td align=center><code>&amp;Dagger;</code></td></tr>
 * <tr><td align=center><code>&circ;</code></td><td align=center><code>&amp;#136;</code></td><td align=center><code>&amp;circ;</code></td></tr>
 * <tr><td align=center><code>&permil;</code></td><td align=center><code>&amp;#137;</code></td><td align=center><code>&amp;permil;</code></td></tr>
 * <tr><td align=center><code>&Scaron;</code></td><td align=center><code>&amp;#138;</code></td><td align=center><code>&amp;Scaron;</code></td></tr>
 * <tr><td align=center><code>&lsaquo;</code></td><td align=center><code>&amp;#139;</code></td><td align=center><code>&amp;lsaquo;</code></td></tr>
 * <tr><td align=center><code>&OElig;</code></td><td align=center><code>&amp;#140;</code></td><td align=center><code>&amp;OElig;</code></td></tr>
 * <tr><td align=center><code></code></td><td align=center><code>&amp;#142;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>&lsquo;</code></td><td align=center><code>&amp;#145;</code></td><td align=center><code>&amp;lsquo;</code></td></tr>
 * <tr><td align=center><code>&rsquo;</code></td><td align=center><code>&amp;#146;</code></td><td align=center><code>&amp;rsquo;</code></td></tr>
 * <tr><td align=center><code>&ldquo;</code></td><td align=center><code>&amp;#147;</code></td><td align=center><code>&amp;ldquo;</code></td></tr>
 * <tr><td align=center><code>&rdquo;</code></td><td align=center><code>&amp;#148;</code></td><td align=center><code>&amp;rdquo;</code></td></tr>
 * <tr><td align=center><code>&bull;</code></td><td align=center><code>&amp;#149;</code></td><td align=center><code>&amp;bull;</code></td></tr>
 * <tr><td align=center><code>&ndash;</code></td><td align=center><code>&amp;#150;</code></td><td align=center><code>&amp;ndash;</code></td></tr>
 * <tr><td align=center><code>&mdash;</code></td><td align=center><code>&amp;#151;</code></td><td align=center><code>&amp;mdash;</code></td></tr>
 * <tr><td align=center><code>&tilde;</code></td><td align=center><code>&amp;#152;</code></td><td align=center><code>&amp;tilde;</code></td></tr>
 * <tr><td align=center><code>&trade;</code></td><td align=center><code>&amp;#153;</code></td><td align=center><code>&amp;trade;</code></td></tr>
 * <tr><td align=center><code>&scaron;</code></td><td align=center><code>&amp;#154;</code></td><td align=center><code>&amp;scaron;</code></td></tr>
 * <tr><td align=center><code>&rsaquo;</code></td><td align=center><code>&amp;#155;</code></td><td align=center><code>&amp;rsaquo;</code></td></tr>
 * <tr><td align=center><code>&oelig;</code></td><td align=center><code>&amp;#156;</code></td><td align=center><code>&amp;oelig;</code></td></tr>
 * <tr><td align=center><code></code></td><td align=center><code>&amp;#158;</code></td><td align=center><code></code></td></tr>
 * <tr><td align=center><code>&Yuml;</code></td><td align=center><code>&amp;#159;</code></td><td align=center><code>&amp;Yuml;</code></td></tr>
 
 
 * <tr><td align=center><code>\u00A0</code></td><td align=center><code>&amp;#160;</code></td><td align=center><code>&amp;nbsp;</code></td></tr>
 
 
 * <tr><td align=center><code>&iexcl;</code></td><td align=center><code>&amp;#161;</code></td><td align=center><code>&amp;iexcl;</code></td></tr>
 * <tr><td align=center><code>&cent;</code></td><td align=center><code>&amp;#162;</code></td><td align=center><code>&amp;cent;</code></td></tr>
 * <tr><td align=center><code>&pound;</code></td><td align=center><code>&amp;#163;</code></td><td align=center><code>&amp;pound;</code></td></tr>
 * <tr><td align=center><code>&curren;</code></td><td align=center><code>&amp;#164;</code></td><td align=center><code>&amp;curren;</code></td></tr>
 * <tr><td align=center><code>&yen;</code></td><td align=center><code>&amp;#165;</code></td><td align=center><code>&amp;yen;</code></td></tr>
 * <tr><td align=center><code>&brvbar;</code></td><td align=center><code>&amp;#166;</code></td><td align=center><code>&amp;brvbar;</code></td></tr>
 * <tr><td align=center><code>&sect;</code></td><td align=center><code>&amp;#167;</code></td><td align=center><code>&amp;sect;</code></td></tr>
 * <tr><td align=center><code>&uml;</code></td><td align=center><code>&amp;#168;</code></td><td align=center><code>&amp;uml;</code></td></tr>
 * <tr><td align=center><code>&copy;</code></td><td align=center><code>&amp;#169;</code></td><td align=center><code>&amp;copy;</code></td></tr>
 * <tr><td align=center><code>&ordf;</code></td><td align=center><code>&amp;#170;</code></td><td align=center><code>&amp;ordf;</code></td></tr>
 * <tr><td align=center><code>&laquo;</code></td><td align=center><code>&amp;#171;</code></td><td align=center><code>&amp;laquo;</code></td></tr>
 * <tr><td align=center><code>&not;</code></td><td align=center><code>&amp;#172;</code></td><td align=center><code>&amp;not;</code></td></tr>
 
 * <tr><td align=center><code>\u00AD</code></td><td align=center><code>&amp;#173;</code></td><td align=center><code>&amp;shy;</code></td></tr>

 
 * <tr><td align=center><code>&reg;</code></td><td align=center><code>&amp;#174;</code></td><td align=center><code>&amp;reg;</code></td></tr>
 * <tr><td align=center><code>&macr;</code></td><td align=center><code>&amp;#175;</code></td><td align=center><code>&amp;macr;</code></td></tr>
 * <tr><td align=center><code>&deg;</code></td><td align=center><code>&amp;#176;</code></td><td align=center><code>&amp;deg;</code></td></tr>
 * <tr><td align=center><code>&plusmn;</code></td><td align=center><code>&amp;#177;</code></td><td align=center><code>&amp;plusmn;</code></td></tr>
 * <tr><td align=center><code>&sup2;</code></td><td align=center><code>&amp;#178;</code></td><td align=center><code>&amp;sup2;</code></td></tr>
 * <tr><td align=center><code>&sup3;</code></td><td align=center><code>&amp;#179;</code></td><td align=center><code>&amp;sup3;</code></td></tr>
 * <tr><td align=center><code>&acute;</code></td><td align=center><code>&amp;#180;</code></td><td align=center><code>&amp;acute;</code></td></tr>
 * <tr><td align=center><code>&micro;</code></td><td align=center><code>&amp;#181;</code></td><td align=center><code>&amp;micro;</code></td></tr>
 * <tr><td align=center><code>&para;</code></td><td align=center><code>&amp;#182;</code></td><td align=center><code>&amp;para;</code></td></tr>
 * <tr><td align=center><code>&middot;</code></td><td align=center><code>&amp;#183;</code></td><td align=center><code>&amp;middot;</code></td></tr>
 * <tr><td align=center><code>&cedil;</code></td><td align=center><code>&amp;#184;</code></td><td align=center><code>&amp;cedil;</code></td></tr>
 * <tr><td align=center><code>&sup1;</code></td><td align=center><code>&amp;#185;</code></td><td align=center><code>&amp;sup1;</code></td></tr>
 * <tr><td align=center><code>&ordm;</code></td><td align=center><code>&amp;#186;</code></td><td align=center><code>&amp;ordm;</code></td></tr>
 * <tr><td align=center><code>&raquo;</code></td><td align=center><code>&amp;#187;</code></td><td align=center><code>&amp;raquo;</code></td></tr>
 * <tr><td align=center><code>&frac14;</code></td><td align=center><code>&amp;#188;</code></td><td align=center><code>&amp;frac14;</code></td></tr>
 * <tr><td align=center><code>&frac12;</code></td><td align=center><code>&amp;#189;</code></td><td align=center><code>&amp;frac12;</code></td></tr>
 * <tr><td align=center><code>&frac34;</code></td><td align=center><code>&amp;#190;</code></td><td align=center><code>&amp;frac34;</code></td></tr>
 * <tr><td align=center><code>&iquest;</code></td><td align=center><code>&amp;#191;</code></td><td align=center><code>&amp;iquest;</code></td></tr>
 * <tr><td align=center><code>&Agrave;</code></td><td align=center><code>&amp;#192;</code></td><td align=center><code>&amp;Agrave;</code></td></tr>
 * <tr><td align=center><code>&Aacute;</code></td><td align=center><code>&amp;#193;</code></td><td align=center><code>&amp;Aacute;</code></td></tr>
 * <tr><td align=center><code>&Acirc;</code></td><td align=center><code>&amp;#194;</code></td><td align=center><code>&amp;Acirc;</code></td></tr>
 * <tr><td align=center><code>&Atilde;</code></td><td align=center><code>&amp;#195;</code></td><td align=center><code>&amp;Atilde;</code></td></tr>
 * <tr><td align=center><code>&Auml;</code></td><td align=center><code>&amp;#196;</code></td><td align=center><code>&amp;Auml;</code></td></tr>
 * <tr><td align=center><code>&Aring;</code></td><td align=center><code>&amp;#197;</code></td><td align=center><code>&amp;Aring;</code></td></tr>
 * <tr><td align=center><code>&AElig;</code></td><td align=center><code>&amp;#198;</code></td><td align=center><code>&amp;AElig;</code></td></tr>
 * <tr><td align=center><code>&Ccedil;</code></td><td align=center><code>&amp;#199;</code></td><td align=center><code>&amp;Ccedil;</code></td></tr>
 * <tr><td align=center><code>&Egrave;</code></td><td align=center><code>&amp;#200;</code></td><td align=center><code>&amp;Egrave;</code></td></tr>
 * <tr><td align=center><code>&Eacute;</code></td><td align=center><code>&amp;#201;</code></td><td align=center><code>&amp;Eacute;</code></td></tr>
 * <tr><td align=center><code>&Ecirc;</code></td><td align=center><code>&amp;#202;</code></td><td align=center><code>&amp;Ecirc;</code></td></tr>
 * <tr><td align=center><code>&Euml;</code></td><td align=center><code>&amp;#203;</code></td><td align=center><code>&amp;Euml;</code></td></tr>
 * <tr><td align=center><code>&Igrave;</code></td><td align=center><code>&amp;#204;</code></td><td align=center><code>&amp;Igrave;</code></td></tr>
 * <tr><td align=center><code>&Iacute;</code></td><td align=center><code>&amp;#205;</code></td><td align=center><code>&amp;Iacute;</code></td></tr>
 * <tr><td align=center><code>&Icirc;</code></td><td align=center><code>&amp;#206;</code></td><td align=center><code>&amp;Icirc;</code></td></tr>
 * <tr><td align=center><code>&Iuml;</code></td><td align=center><code>&amp;#207;</code></td><td align=center><code>&amp;Iuml;</code></td></tr>
 * <tr><td align=center><code>&ETH;</code></td><td align=center><code>&amp;#208;</code></td><td align=center><code>&amp;ETH;</code></td></tr>
 * <tr><td align=center><code>&Ntilde;</code></td><td align=center><code>&amp;#209;</code></td><td align=center><code>&amp;Ntilde;</code></td></tr>
 * <tr><td align=center><code>&Ograve;</code></td><td align=center><code>&amp;#210;</code></td><td align=center><code>&amp;Ograve;</code></td></tr>
 * <tr><td align=center><code>&Oacute;</code></td><td align=center><code>&amp;#211;</code></td><td align=center><code>&amp;Oacute;</code></td></tr>
 * <tr><td align=center><code>&Ocirc;</code></td><td align=center><code>&amp;#212;</code></td><td align=center><code>&amp;Ocirc;</code></td></tr>
 * <tr><td align=center><code>&Otilde;</code></td><td align=center><code>&amp;#213;</code></td><td align=center><code>&amp;Otilde;</code></td></tr>
 * <tr><td align=center><code>&Ouml;</code></td><td align=center><code>&amp;#214;</code></td><td align=center><code>&amp;Ouml;</code></td></tr>
 * <tr><td align=center><code>&times;</code></td><td align=center><code>&amp;#215;</code></td><td align=center><code>&amp;times;</code></td></tr>
 * <tr><td align=center><code>&Oslash;</code></td><td align=center><code>&amp;#216;</code></td><td align=center><code>&amp;Oslash;</code></td></tr>
 * <tr><td align=center><code>&Ugrave;</code></td><td align=center><code>&amp;#217;</code></td><td align=center><code>&amp;Ugrave;</code></td></tr>
 * <tr><td align=center><code>&Uacute;</code></td><td align=center><code>&amp;#218;</code></td><td align=center><code>&amp;Uacute;</code></td></tr>
 * <tr><td align=center><code>&Ucirc;</code></td><td align=center><code>&amp;#219;</code></td><td align=center><code>&amp;Ucirc;</code></td></tr>
 * <tr><td align=center><code>&Uuml;</code></td><td align=center><code>&amp;#220;</code></td><td align=center><code>&amp;Uuml;</code></td></tr>
 * <tr><td align=center><code>&Yacute;</code></td><td align=center><code>&amp;#221;</code></td><td align=center><code>&amp;Yacute;</code></td></tr>
 * <tr><td align=center><code>&THORN;</code></td><td align=center><code>&amp;#222;</code></td><td align=center><code>&amp;THORN;</code></td></tr>
 * <tr><td align=center><code>&szlig;</code></td><td align=center><code>&amp;#223;</code></td><td align=center><code>&amp;szlig;</code></td></tr>
 * <tr><td align=center><code>&agrave;</code></td><td align=center><code>&amp;#224;</code></td><td align=center><code>&amp;agrave;</code></td></tr>
 * <tr><td align=center><code>&aacute;</code></td><td align=center><code>&amp;#225;</code></td><td align=center><code>&amp;aacute;</code></td></tr>
 * <tr><td align=center><code>&acirc;</code></td><td align=center><code>&amp;#226;</code></td><td align=center><code>&amp;acirc;</code></td></tr>
 * <tr><td align=center><code>&atilde;</code></td><td align=center><code>&amp;#227;</code></td><td align=center><code>&amp;atilde;</code></td></tr>
 * <tr><td align=center><code>&auml;</code></td><td align=center><code>&amp;#228;</code></td><td align=center><code>&amp;auml;</code></td></tr>
 * <tr><td align=center><code>&aring;</code></td><td align=center><code>&amp;#229;</code></td><td align=center><code>&amp;aring;</code></td></tr>
 * <tr><td align=center><code>&aelig;</code></td><td align=center><code>&amp;#230;</code></td><td align=center><code>&amp;aelig;</code></td></tr>
 * <tr><td align=center><code>&ccedil;</code></td><td align=center><code>&amp;#231;</code></td><td align=center><code>&amp;ccedil;</code></td></tr>
 * <tr><td align=center><code>&egrave;</code></td><td align=center><code>&amp;#232;</code></td><td align=center><code>&amp;egrave;</code></td></tr>
 * <tr><td align=center><code>&eacute;</code></td><td align=center><code>&amp;#233;</code></td><td align=center><code>&amp;eacute;</code></td></tr>
 * <tr><td align=center><code>&ecirc;</code></td><td align=center><code>&amp;#234;</code></td><td align=center><code>&amp;ecirc;</code></td></tr>
 * <tr><td align=center><code>&euml;</code></td><td align=center><code>&amp;#235;</code></td><td align=center><code>&amp;euml;</code></td></tr>
 * <tr><td align=center><code>&igrave;</code></td><td align=center><code>&amp;#236;</code></td><td align=center><code>&amp;igrave;</code></td></tr>
 * <tr><td align=center><code>&iacute;</code></td><td align=center><code>&amp;#237;</code></td><td align=center><code>&amp;iacute;</code></td></tr>
 * <tr><td align=center><code>&icirc;</code></td><td align=center><code>&amp;#238;</code></td><td align=center><code>&amp;icirc;</code></td></tr>
 * <tr><td align=center><code>&iuml;</code></td><td align=center><code>&amp;#239;</code></td><td align=center><code>&amp;iuml;</code></td></tr>
 * <tr><td align=center><code>&eth;</code></td><td align=center><code>&amp;#240;</code></td><td align=center><code>&amp;eth;</code></td></tr>
 * <tr><td align=center><code>&ntilde;</code></td><td align=center><code>&amp;#241;</code></td><td align=center><code>&amp;ntilde;</code></td></tr>
 * <tr><td align=center><code>&ograve;</code></td><td align=center><code>&amp;#242;</code></td><td align=center><code>&amp;ograve;</code></td></tr>
 * <tr><td align=center><code>&oacute;</code></td><td align=center><code>&amp;#243;</code></td><td align=center><code>&amp;oacute;</code></td></tr>
 * <tr><td align=center><code>&ocirc;</code></td><td align=center><code>&amp;#244;</code></td><td align=center><code>&amp;ocirc;</code></td></tr>
 * <tr><td align=center><code>&otilde;</code></td><td align=center><code>&amp;#245;</code></td><td align=center><code>&amp;otilde;</code></td></tr>
 * <tr><td align=center><code>&ouml;</code></td><td align=center><code>&amp;#246;</code></td><td align=center><code>&amp;ouml;</code></td></tr>
 * <tr><td align=center><code>&divide;</code></td><td align=center><code>&amp;#247;</code></td><td align=center><code>&amp;divide;</code></td></tr>
 * <tr><td align=center><code>&oslash;</code></td><td align=center><code>&amp;#248;</code></td><td align=center><code>&amp;oslash;</code></td></tr>
 * <tr><td align=center><code>&ugrave;</code></td><td align=center><code>&amp;#249;</code></td><td align=center><code>&amp;ugrave;</code></td></tr>
 * <tr><td align=center><code>&uacute;</code></td><td align=center><code>&amp;#250;</code></td><td align=center><code>&amp;uacute;</code></td></tr>
 * <tr><td align=center><code>&ucirc;</code></td><td align=center><code>&amp;#251;</code></td><td align=center><code>&amp;ucirc;</code></td></tr>
 * <tr><td align=center><code>&uuml;</code></td><td align=center><code>&amp;#252;</code></td><td align=center><code>&amp;uuml;</code></td></tr>
 * <tr><td align=center><code>&yacute;</code></td><td align=center><code>&amp;#253;</code></td><td align=center><code>&amp;yacute;</code></td></tr>
 * <tr><td align=center><code>&thorn;</code></td><td align=center><code>&amp;#254;</code></td><td align=center><code>&amp;thorn;</code></td></tr>
 * <tr><td align=center><code>&yuml;</code></td><td align=center><code>&amp;#255;</code></td><td align=center><code>&amp;yuml;</code></td></tr>

 
 
 
 * </table>
 * </blockquote>
 * <br>
 * <br>
 * @author Eric Bishop<br>
 * @creation Feb 10, 2015<br>
 *
 */
public class BSpecialHtmlCharStringToString extends BComponent implements BIStatusValue
{
	public static final Property facets = newProperty(0, BFacets.DEFAULT);
	public BFacets getFacets() { return (BFacets)get(facets); }
	public void setFacets(BFacets v) { set(facets,v,null); }
	
	public final static Property unescapeUrlEncoding = newProperty(0, new BStatusBoolean(false));
	public BStatusBoolean getUnescapeUrlEncoding() { return (BStatusBoolean)get(unescapeUrlEncoding); }
	public void setUnescapeUrlEncoding(BStatusBoolean v) { set(unescapeUrlEncoding, v); }
	
	public static final Property in = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getIn() { return (BStatusString)get(in); }
	public void setIn(BStatusString v) { set(in,v); }

	public static final Property out = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getOut() { return (BStatusString)get(out); }
	public void setOut(BStatusString v) { set(out,v); }

	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BSpecialHtmlCharStringToString.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/EB.png");
	
	public void started() throws Exception
	{
		super.started();
		if(!Sys.atSteadyState() || !isRunning()) return;
		getOut().setStatus(getIn().getStatus());
		getOut().setValue(formatHtmlString(getIn().getValue()));
	}

	public void atSteadyState() throws Exception
	{
		if(!Sys.atSteadyState() || !isRunning()) return;
		getOut().setStatus(getIn().getStatus());
		getOut().setValue(formatHtmlString(getIn().getValue()));
	}

	public void changed(Property p, Context cx)
	{
		super.changed(p, cx);
		if(!Sys.atSteadyState() || !isRunning()) return;
		if(!p.equals(in)) return;
		
		if(getUnescapeUrlEncoding().getValue()==true)
		{
			getOut().setStatus(getIn().getStatus());
			getOut().setValue(unescape(formatHtmlString(getIn().getValue())));
		}
		else
		{
			getOut().setStatus(getIn().getStatus());
			getOut().setValue(formatHtmlString(getIn().getValue()));
		}
	}
	
	String escape(String s){return com.tridium.util.EscUtil.slot.escape(s);}
	String unescape(String s){return com.tridium.util.EscUtil.slot.unescape(s);}
	 
	public static final String formatHtmlString(String strInput)
	{
		//HTML NAMES.......
		strInput = replaceString(strInput, "&excl;", "\u0021");
		strInput = replaceString(strInput, "&quot;", "\\u0022");
		strInput = replaceString(strInput, "&num;", "\u0023");
		strInput = replaceString(strInput, "&dollar;", "\u0024");
		strInput = replaceString(strInput, "&percnt;", "\u0025");
		strInput = replaceString(strInput, "&amp;", "\u0026");
		strInput = replaceString(strInput, "&apos;", "\u0027");
		strInput = replaceString(strInput, "&lpar;", "\u0028");
		strInput = replaceString(strInput, "&rpar;", "\u0029");
		strInput = replaceString(strInput, "&ast;", "\u002A");
		strInput = replaceString(strInput, "&comma;", "\u002C");
		strInput = replaceString(strInput, "&period;", "\u002E");
		strInput = replaceString(strInput, "&sol;", "\u002F");
		strInput = replaceString(strInput, "&colon;", "\u003A");
		strInput = replaceString(strInput, "&semi;", "\u003B");
		strInput = replaceString(strInput, "&lt;", "\u003C");
		strInput = replaceString(strInput, "&equals;", "\u003D");
		strInput = replaceString(strInput, "&gt;", "\u003E");
		strInput = replaceString(strInput, "&quest;", "\u003F");
		strInput = replaceString(strInput, "&commat;", "\u0040");
		strInput = replaceString(strInput, "&lbrack;", "\u005B");
		strInput = replaceString(strInput, "&bsol;", "\u005C\u005C");
		strInput = replaceString(strInput, "&rbrack;", "\u005D");
		strInput = replaceString(strInput, "&Hat;", "\u005E");
		strInput = replaceString(strInput, "&lowbar;", "\u005F");
		strInput = replaceString(strInput, "&DiacriticalGrave; ", "\u0060");
		strInput = replaceString(strInput, "&lbrace;", "\u007B");
		strInput = replaceString(strInput, "&verbar;", "\u007C");
		strInput = replaceString(strInput, "&rbrace;", "\u007D");
		strInput = replaceString(strInput, "&euro;", "\u0080");
		strInput = replaceString(strInput, "&uuml;", "\u0081");
		strInput = replaceString(strInput, "&sbquo;", "\u0082");
		strInput = replaceString(strInput, "&fnof;", "\u0083");
		strInput = replaceString(strInput, "&bdquo;", "\u0084");
		strInput = replaceString(strInput, "&hellip;", "\u0085");
		strInput = replaceString(strInput, "&dagger;", "\u0086");
		strInput = replaceString(strInput, "&Dagger;", "\u0087");
		strInput = replaceString(strInput, "&circ;", "\u0088");
		strInput = replaceString(strInput, "&permil;", "\u0089");
		strInput = replaceString(strInput, "&Scaron;", "\u008A");
		strInput = replaceString(strInput, "&lsaquo;", "\u008B");
		strInput = replaceString(strInput, "&OElig;", "\u008C");
		strInput = replaceString(strInput, "&Zcaron;", "\u008E");
		strInput = replaceString(strInput, "&lsquo;", "\u0091");
		strInput = replaceString(strInput, "&rsquo;", "\u0092");
		strInput = replaceString(strInput, "&ldquo;", "\u0093");
		strInput = replaceString(strInput, "&rdquo;", "\u0094");
		strInput = replaceString(strInput, "&bull;", "\u0095");
		strInput = replaceString(strInput, "&ndash;", "\u0096");
		strInput = replaceString(strInput, "&mdash;", "\u0097");
		strInput = replaceString(strInput, "&tilde;", "\u0098");
		strInput = replaceString(strInput, "&trade;", "\u0099");
		strInput = replaceString(strInput, "&scaron;", "\u009A");
		strInput = replaceString(strInput, "&rsaquo;", "\u009B");
		strInput = replaceString(strInput, "&oelig;", "\u009C");
		strInput = replaceString(strInput, "&Yuml;", "\u009F");
		strInput = replaceString(strInput, "&nbsp;", "\u00A0");
		strInput = replaceString(strInput, "&iexcl;", "\u00A1");
		strInput = replaceString(strInput, "&cent;", "\u00A2");
		strInput = replaceString(strInput, "&pound;", "\u00A3");
		strInput = replaceString(strInput, "&curren;", "\u00A4");
		strInput = replaceString(strInput, "&yen;", "\u00A5");
		strInput = replaceString(strInput, "&brvbar;", "\u00A6");
		strInput = replaceString(strInput, "&sect;", "\u00A7");
		strInput = replaceString(strInput, "&uml;", "\u00A8");
		strInput = replaceString(strInput, "&copy;", "\u00A9");
		strInput = replaceString(strInput, "&ordf;", "\u00AA");
		strInput = replaceString(strInput, "&laquo;", "\u00AB");
		strInput = replaceString(strInput, "&not;", "\u00AC");
		strInput = replaceString(strInput, "&shy;", "\u00AD");
		strInput = replaceString(strInput, "&reg;", "\u00AE");
		strInput = replaceString(strInput, "&macr;", "\u00AF");
		strInput = replaceString(strInput, "&deg;", "\u00B0");
		strInput = replaceString(strInput, "&plusmn;", "\u00B1");
		strInput = replaceString(strInput, "&sup2;", "\u00B2");
		strInput = replaceString(strInput, "&sup3;", "\u00B3");
		strInput = replaceString(strInput, "&acute;", "\u00B4");
		strInput = replaceString(strInput, "&micro;", "\u00B5");
		strInput = replaceString(strInput, "&para;", "\u00B6");
		strInput = replaceString(strInput, "&middot;", "\u00B7");
		strInput = replaceString(strInput, "&cedil;", "\u00B8");
		strInput = replaceString(strInput, "&sup1;", "\u00B9");
		strInput = replaceString(strInput, "&ordm;", "\u00BA");
		strInput = replaceString(strInput, "&raquo;", "\u00BB");
		strInput = replaceString(strInput, "&frac14;", "\u00BC");
		strInput = replaceString(strInput, "&frac12;", "\u00BD");
		strInput = replaceString(strInput, "&frac34;", "\u00BE");
		strInput = replaceString(strInput, "&iquest;", "\u00BF");
		strInput = replaceString(strInput, "&Agrave;", "\u00C0");
		strInput = replaceString(strInput, "&Aacute;", "\u00C1");
		strInput = replaceString(strInput, "&Acirc;", "\u00C2");
		strInput = replaceString(strInput, "&Atilde;", "\u00C3");
		strInput = replaceString(strInput, "&Auml;", "\u00C4");
		strInput = replaceString(strInput, "&Aring;", "\u00C5");
		strInput = replaceString(strInput, "&AElig;", "\u00C6");
		strInput = replaceString(strInput, "&Ccedil;", "\u00C7");
		strInput = replaceString(strInput, "&Egrave;", "\u00C8");
		strInput = replaceString(strInput, "&Eacute;", "\u00C9");
		strInput = replaceString(strInput, "&Ecirc;", "\u00CA");
		strInput = replaceString(strInput, "&Euml;", "\u00CB");
		strInput = replaceString(strInput, "&Igrave;", "\u00CC");
		strInput = replaceString(strInput, "&Iacute;", "\u00CD");
		strInput = replaceString(strInput, "&Icirc;", "\u00CE");
		strInput = replaceString(strInput, "&Iuml;", "\u00CF");
		strInput = replaceString(strInput, "&ETH;", "\u00D0");
		strInput = replaceString(strInput, "&Ntilde;", "\u00D1");
		strInput = replaceString(strInput, "&Ograve;", "\u00D2");
		strInput = replaceString(strInput, "&Oacute;", "\u00D3");
		strInput = replaceString(strInput, "&Ocirc;", "\u00D4");
		strInput = replaceString(strInput, "&Otilde;", "\u00D5");
		strInput = replaceString(strInput, "&Ouml;", "\u00D6");
		strInput = replaceString(strInput, "&times;", "\u00D7");
		strInput = replaceString(strInput, "&Oslash;", "\u00D8");
		strInput = replaceString(strInput, "&Ugrave;", "\u00D9");
		strInput = replaceString(strInput, "&Uacute;", "\u00DA");
		strInput = replaceString(strInput, "&Ucirc;", "\u00DB");
		strInput = replaceString(strInput, "&Uuml;", "\u00DC");
		strInput = replaceString(strInput, "&Yacute;", "\u00DD");
		strInput = replaceString(strInput, "&THORN;", "\u00DE");
		strInput = replaceString(strInput, "&szlig;", "\u00DF");
		strInput = replaceString(strInput, "&agrave;", "\u00E0");
		strInput = replaceString(strInput, "&aacute;", "\u00E1");
		strInput = replaceString(strInput, "&acirc;", "\u00E2");
		strInput = replaceString(strInput, "&atilde;", "\u00E3");
		strInput = replaceString(strInput, "&auml;", "\u00E4");
		strInput = replaceString(strInput, "&aring;", "\u00E5");
		strInput = replaceString(strInput, "&aelig;", "\u00E6");
		strInput = replaceString(strInput, "&ccedil;", "\u00E7");
		strInput = replaceString(strInput, "&egrave;", "\u00E8");
		strInput = replaceString(strInput, "&eacute;", "\u00E9");
		strInput = replaceString(strInput, "&ecirc;", "\u00EA");
		strInput = replaceString(strInput, "&euml;", "\u00EB");
		strInput = replaceString(strInput, "&igrave;", "\u00EC");
		strInput = replaceString(strInput, "&iacute;", "\u00ED");
		strInput = replaceString(strInput, "&icirc;", "\u00EE");
		strInput = replaceString(strInput, "&iuml;", "\u00EF");
		strInput = replaceString(strInput, "&eth;", "\u00F0");
		strInput = replaceString(strInput, "&ntilde;", "\u00F1");
		strInput = replaceString(strInput, "&ograve;", "\u00F2");
		strInput = replaceString(strInput, "&oacute;", "\u00F3");
		strInput = replaceString(strInput, "&ocirc;", "\u00F4");
		strInput = replaceString(strInput, "&otilde;", "\u00F5");
		strInput = replaceString(strInput, "&ouml;", "\u00F6");
		strInput = replaceString(strInput, "&divide;", "\u00F7");
		strInput = replaceString(strInput, "&oslash;", "\u00F8");
		strInput = replaceString(strInput, "&ugrave;", "\u00F9");
		strInput = replaceString(strInput, "&uacute;", "\u00FA");
		strInput = replaceString(strInput, "&ucirc;", "\u00FB");
		strInput = replaceString(strInput, "&uuml;", "\u00FC");
		strInput = replaceString(strInput, "&yacute;", "\u00FD");
		strInput = replaceString(strInput, "&thorn;", "\u00FE");
		strInput = replaceString(strInput, "&yuml;", "\u00FF");



		//HTML NUMBERS.......
		strInput = replaceString(strInput, "&#32;", "\u0020");
		strInput = replaceString(strInput, "&#33;", "\u0021");
		strInput = replaceString(strInput, "&#34;", "\\u0022");
		strInput = replaceString(strInput, "&#35;", "\u0023");
		strInput = replaceString(strInput, "&#36;", "\u0024");
		strInput = replaceString(strInput, "&#37;", "\u0025");
		strInput = replaceString(strInput, "&#38;", "\u0026");
		strInput = replaceString(strInput, "&#39;", "\u0027");
		strInput = replaceString(strInput, "&#40;", "\u0028");
		strInput = replaceString(strInput, "&#41;", "\u0029");
		strInput = replaceString(strInput, "&#42;", "\u002A");
		strInput = replaceString(strInput, "&#43;", "\u002B");
		strInput = replaceString(strInput, "&#44;", "\u002C");
		strInput = replaceString(strInput, "&#45;", "\u002D");
		strInput = replaceString(strInput, "&#46;", "\u002E");
		strInput = replaceString(strInput, "&#47;", "\u002F");
		strInput = replaceString(strInput, "&#48;", "\u0030");
		strInput = replaceString(strInput, "&#49;", "\u0031");
		strInput = replaceString(strInput, "&#50;", "\u0032");
		strInput = replaceString(strInput, "&#51;", "\u0033");
		strInput = replaceString(strInput, "&#52;", "\u0034");
		strInput = replaceString(strInput, "&#53;", "\u0035");
		strInput = replaceString(strInput, "&#54;", "\u0036");
		strInput = replaceString(strInput, "&#55;", "\u0037");
		strInput = replaceString(strInput, "&#56;", "\u0038");
		strInput = replaceString(strInput, "&#57;", "\u0039");
		strInput = replaceString(strInput, "&#58;", "\u003A");
		strInput = replaceString(strInput, "&#59;", "\u003B");
		strInput = replaceString(strInput, "&#60;", "\u003C");
		strInput = replaceString(strInput, "&#61;", "\u003D");
		strInput = replaceString(strInput, "&#62;", "\u003E");
		strInput = replaceString(strInput, "&#63;", "\u003F");
		strInput = replaceString(strInput, "&#64;", "\u0040");
		strInput = replaceString(strInput, "&#65;", "\u0041");
		strInput = replaceString(strInput, "&#66;", "\u0042");
		strInput = replaceString(strInput, "&#67;", "\u0043");
		strInput = replaceString(strInput, "&#68;", "\u0044");
		strInput = replaceString(strInput, "&#69;", "\u0045");
		strInput = replaceString(strInput, "&#70;", "\u0046");
		strInput = replaceString(strInput, "&#71;", "\u0047");
		strInput = replaceString(strInput, "&#72;", "\u0048");
		strInput = replaceString(strInput, "&#73;", "\u0049");
		strInput = replaceString(strInput, "&#74;", "\u004A");
		strInput = replaceString(strInput, "&#75;", "\u004B");
		strInput = replaceString(strInput, "&#76;", "\u004C");
		strInput = replaceString(strInput, "&#77;", "\u004D");
		strInput = replaceString(strInput, "&#78;", "\u004E");
		strInput = replaceString(strInput, "&#79;", "\u004F");
		strInput = replaceString(strInput, "&#80;", "\u0050");
		strInput = replaceString(strInput, "&#81;", "\u0051");
		strInput = replaceString(strInput, "&#82;", "\u0052");
		strInput = replaceString(strInput, "&#83;", "\u0053");
		strInput = replaceString(strInput, "&#84;", "\u0054");
		strInput = replaceString(strInput, "&#85;", "\u0055");
		strInput = replaceString(strInput, "&#86;", "\u0056");
		strInput = replaceString(strInput, "&#87;", "\u0057");
		strInput = replaceString(strInput, "&#88;", "\u0058");
		strInput = replaceString(strInput, "&#89;", "\u0059");
		strInput = replaceString(strInput, "&#90;", "\u005A");
		strInput = replaceString(strInput, "&#91;", "\u005B");
		strInput = replaceString(strInput, "&#92;", "\u005C\u005C");
		strInput = replaceString(strInput, "&#93;", "\u005D");
		strInput = replaceString(strInput, "&#94;", "\u005E");
		strInput = replaceString(strInput, "&#95;", "\u005F");
		strInput = replaceString(strInput, "&#96;", "\u0060");
		strInput = replaceString(strInput, "&#97;", "\u0061");
		strInput = replaceString(strInput, "&#98;", "\u0062");
		strInput = replaceString(strInput, "&#99;", "\u0063");
		strInput = replaceString(strInput, "&#100;", "\u0064");
		strInput = replaceString(strInput, "&#101;", "\u0065");
		strInput = replaceString(strInput, "&#102;", "\u0066");
		strInput = replaceString(strInput, "&#103;", "\u0067");
		strInput = replaceString(strInput, "&#104;", "\u0068");
		strInput = replaceString(strInput, "&#105;", "\u0069");
		strInput = replaceString(strInput, "&#106;", "\u006A");
		strInput = replaceString(strInput, "&#107;", "\u006B");
		strInput = replaceString(strInput, "&#108;", "\u006C");
		strInput = replaceString(strInput, "&#109;", "\u006D");
		strInput = replaceString(strInput, "&#110;", "\u006E");
		strInput = replaceString(strInput, "&#111;", "\u006F");
		strInput = replaceString(strInput, "&#112;", "\u0070");
		strInput = replaceString(strInput, "&#113;", "\u0071");
		strInput = replaceString(strInput, "&#114;", "\u0072");
		strInput = replaceString(strInput, "&#115;", "\u0073");
		strInput = replaceString(strInput, "&#116;", "\u0074");
		strInput = replaceString(strInput, "&#117;", "\u0075");
		strInput = replaceString(strInput, "&#118;", "\u0076");
		strInput = replaceString(strInput, "&#119;", "\u0077");
		strInput = replaceString(strInput, "&#120;", "\u0078");
		strInput = replaceString(strInput, "&#121;", "\u0079");
		strInput = replaceString(strInput, "&#122;", "\u007A");
		strInput = replaceString(strInput, "&#123;", "\u007B");
		strInput = replaceString(strInput, "&#124;", "\u007C");
		strInput = replaceString(strInput, "&#125;", "\u007D");
		strInput = replaceString(strInput, "&#126;", "\u007E");
		strInput = replaceString(strInput, "&#127;", "\u007F");
		strInput = replaceString(strInput, "&#128;", "\u0080");
		strInput = replaceString(strInput, "&#130;", "\u0082");
		strInput = replaceString(strInput, "&#131;", "\u0083");
		strInput = replaceString(strInput, "&#132;", "\u0084");
		strInput = replaceString(strInput, "&#133;", "\u0085");
		strInput = replaceString(strInput, "&#134;", "\u0086");
		strInput = replaceString(strInput, "&#135;", "\u0087");
		strInput = replaceString(strInput, "&#136;", "\u0088");
		strInput = replaceString(strInput, "&#137;", "\u0089");
		strInput = replaceString(strInput, "&#138;", "\u008A");
		strInput = replaceString(strInput, "&#139;", "\u008B");
		strInput = replaceString(strInput, "&#140;", "\u008C");
		strInput = replaceString(strInput, "&#142;", "\u008E");
		strInput = replaceString(strInput, "&#145;", "\u0091");
		strInput = replaceString(strInput, "&#146;", "\u0092");
		strInput = replaceString(strInput, "&#147;", "\u0093");
		strInput = replaceString(strInput, "&#148;", "\u0094");
		strInput = replaceString(strInput, "&#149;", "\u0095");
		strInput = replaceString(strInput, "&#150;", "\u0096");
		strInput = replaceString(strInput, "&#151;", "\u0097");
		strInput = replaceString(strInput, "&#152;", "\u0098");
		strInput = replaceString(strInput, "&#153;", "\u0099");
		strInput = replaceString(strInput, "&#154;", "\u009A");
		strInput = replaceString(strInput, "&#155;", "\u009B");
		strInput = replaceString(strInput, "&#156;", "\u009C");
		strInput = replaceString(strInput, "&#158;", "\u009E");
		strInput = replaceString(strInput, "&#159;", "\u009F");
		strInput = replaceString(strInput, "&#160;", "\u00A0");
		strInput = replaceString(strInput, "&#161;", "\u00A1");
		strInput = replaceString(strInput, "&#162;", "\u00A2");
		strInput = replaceString(strInput, "&#163;", "\u00A3");
		strInput = replaceString(strInput, "&#164;", "\u00A4");
		strInput = replaceString(strInput, "&#165;", "\u00A5");
		strInput = replaceString(strInput, "&#166;", "\u00A6");
		strInput = replaceString(strInput, "&#167;", "\u00A7");
		strInput = replaceString(strInput, "&#168;", "\u00A8");
		strInput = replaceString(strInput, "&#169;", "\u00A9");
		strInput = replaceString(strInput, "&#170;", "\u00AA");
		strInput = replaceString(strInput, "&#171;", "\u00AB");
		strInput = replaceString(strInput, "&#172;", "\u00AC");
		strInput = replaceString(strInput, "&#173;", "\u00AD");
		strInput = replaceString(strInput, "&#174;", "\u00AE");
		strInput = replaceString(strInput, "&#175;", "\u00AF");
		strInput = replaceString(strInput, "&#176;", "\u00B0");
		strInput = replaceString(strInput, "&#177;", "\u00B1");
		strInput = replaceString(strInput, "&#178;", "\u00B2");
		strInput = replaceString(strInput, "&#179;", "\u00B3");
		strInput = replaceString(strInput, "&#180;", "\u00B4");
		strInput = replaceString(strInput, "&#181;", "\u00B5");
		strInput = replaceString(strInput, "&#182;", "\u00B6");
		strInput = replaceString(strInput, "&#183;", "\u00B7");
		strInput = replaceString(strInput, "&#184;", "\u00B8");
		strInput = replaceString(strInput, "&#185;", "\u00B9");
		strInput = replaceString(strInput, "&#186;", "\u00BA");
		strInput = replaceString(strInput, "&#187;", "\u00BB");
		strInput = replaceString(strInput, "&#188;", "\u00BC");
		strInput = replaceString(strInput, "&#189;", "\u00BD");
		strInput = replaceString(strInput, "&#190;", "\u00BE");
		strInput = replaceString(strInput, "&#191;", "\u00BF");
		strInput = replaceString(strInput, "&#192;", "\u00C0");
		strInput = replaceString(strInput, "&#193;", "\u00C1");
		strInput = replaceString(strInput, "&#194;", "\u00C2");
		strInput = replaceString(strInput, "&#195;", "\u00C3");
		strInput = replaceString(strInput, "&#196;", "\u00C4");
		strInput = replaceString(strInput, "&#197;", "\u00C5");
		strInput = replaceString(strInput, "&#198;", "\u00C6");
		strInput = replaceString(strInput, "&#199;", "\u00C7");
		strInput = replaceString(strInput, "&#200;", "\u00C8");
		strInput = replaceString(strInput, "&#201;", "\u00C9");
		strInput = replaceString(strInput, "&#202;", "\u00CA");
		strInput = replaceString(strInput, "&#203;", "\u00CB");
		strInput = replaceString(strInput, "&#204;", "\u00CC");
		strInput = replaceString(strInput, "&#205;", "\u00CD");
		strInput = replaceString(strInput, "&#206;", "\u00CE");
		strInput = replaceString(strInput, "&#207;", "\u00CF");
		strInput = replaceString(strInput, "&#208;", "\u00D0");
		strInput = replaceString(strInput, "&#209;", "\u00D1");
		strInput = replaceString(strInput, "&#210;", "\u00D2");
		strInput = replaceString(strInput, "&#211;", "\u00D3");
		strInput = replaceString(strInput, "&#212;", "\u00D4");
		strInput = replaceString(strInput, "&#213;", "\u00D5");
		strInput = replaceString(strInput, "&#214;", "\u00D6");
		strInput = replaceString(strInput, "&#215;", "\u00D7");
		strInput = replaceString(strInput, "&#216;", "\u00D8");
		strInput = replaceString(strInput, "&#217;", "\u00D9");
		strInput = replaceString(strInput, "&#218;", "\u00DA");
		strInput = replaceString(strInput, "&#219;", "\u00DB");
		strInput = replaceString(strInput, "&#220;", "\u00DC");
		strInput = replaceString(strInput, "&#221;", "\u00DD");
		strInput = replaceString(strInput, "&#222;", "\u00DE");
		strInput = replaceString(strInput, "&#223;", "\u00DF");
		strInput = replaceString(strInput, "&#224;", "\u00E0");
		strInput = replaceString(strInput, "&#225;", "\u00E1");
		strInput = replaceString(strInput, "&#226;", "\u00E2");
		strInput = replaceString(strInput, "&#227;", "\u00E3");
		strInput = replaceString(strInput, "&#228;", "\u00E4");
		strInput = replaceString(strInput, "&#229;", "\u00E5");
		strInput = replaceString(strInput, "&#230;", "\u00E6");
		strInput = replaceString(strInput, "&#231;", "\u00E7");
		strInput = replaceString(strInput, "&#232;", "\u00E8");
		strInput = replaceString(strInput, "&#233;", "\u00E9");
		strInput = replaceString(strInput, "&#234;", "\u00EA");
		strInput = replaceString(strInput, "&#235;", "\u00EB");
		strInput = replaceString(strInput, "&#236;", "\u00EC");
		strInput = replaceString(strInput, "&#237;", "\u00ED");
		strInput = replaceString(strInput, "&#238;", "\u00EE");
		strInput = replaceString(strInput, "&#239;", "\u00EF");
		strInput = replaceString(strInput, "&#240;", "\u00F0");
		strInput = replaceString(strInput, "&#241;", "\u00F1");
		strInput = replaceString(strInput, "&#242;", "\u00F2");
		strInput = replaceString(strInput, "&#243;", "\u00F3");
		strInput = replaceString(strInput, "&#244;", "\u00F4");
		strInput = replaceString(strInput, "&#245;", "\u00F5");
		strInput = replaceString(strInput, "&#246;", "\u00F6");
		strInput = replaceString(strInput, "&#247;", "\u00F7");
		strInput = replaceString(strInput, "&#248;", "\u00F8");
		strInput = replaceString(strInput, "&#249;", "\u00F9");
		strInput = replaceString(strInput, "&#250;", "\u00FA");
		strInput = replaceString(strInput, "&#251;", "\u00FB");
		strInput = replaceString(strInput, "&#252;", "\u00FC");
		strInput = replaceString(strInput, "&#253;", "\u00FD");
		strInput = replaceString(strInput, "&#254;", "\u00FE");
		strInput = replaceString(strInput, "&#255;", "\u00FF");


		return strInput;
	}
	
	
	public static String replaceString(String sourceStr, String oldStr, String newStr)
	{
		int idx = sourceStr.lastIndexOf(oldStr);
		if (idx != -1) 
		{
			StringBuffer results = new StringBuffer(sourceStr);
			results.replace( idx, idx+oldStr.length(), newStr);
			while( (idx=sourceStr.lastIndexOf(oldStr, idx-1)) != -1 ) results.replace(idx, idx+oldStr.length(), newStr);
			
			return results.toString();
		}
		else return sourceStr;
	}
	
	
	public BFacets getSlotFacets(Slot slot) { return super.getSlotFacets(slot); }
	public BStatus getStatus() { return getOut().getStatus(); }
	public BStatusValue getStatusValue() { return getOut().getStatusValue(); }
	public BFacets getStatusValueFacets() { return getFacets(); }
	public BValue getActionParameterDefault(Action action) { return super.getActionParameterDefault(action); }
}
