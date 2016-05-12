package org.axcommunity.niagara.conversion;

import javax.baja.status.BIStatusValue;
import javax.baja.status.BStatus;
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
 * &amp;    = &<br>
 * &quot;   = "<br>
 * &lt;     = <<br>
 * &gt;     = ><br>
 * &euro;   = €<br>
 * &sbquo;  = ‚<br>
 * &fnof;   = ƒ<br>
 * &bdquo;  = „<br>
 * &hellip; = …<br>
 * &dagger; = †<br>
 * &Dagger; = ‡<br>
 * &circ;   = ˆ<br>
 * &permil; = ‰<br>
 * &Scaron; = Š<br>
 * &lsaquo; = ‹<br>
 * &OElig;  = Œ<br>
 * &lsquo;  = ‘<br>
 * &rsquo;  = ’<br>
 * &ldquo;  = “<br>
 * &rdquo;  = ”<br>
 * &bull;   = •<br>
 * &ndash;  = –<br>
 * &mdash;  = —<br>
 * &tilde;  = ˜<br>
 * &trade;  = ™<br>
 * &scaron; = š<br>
 * &rsaquo; = ›<br>
 * &oelig;  = œ<br>
 * &yuml;   = Ÿ<br>
 * &nbsp;   = <br>
 * &iexcl;  = ¡<br>
 * &cent;   = ¢<br>
 * &pound;  = £<br>
 * &curren; = ¤<br>
 * &yen;    = ¥<br>
 * &brvbar; = ¦<br>
 * &sect;   = §<br>
 * &uml;    = ¨<br>
 * &copy;   = ©<br>
 * &ordf;   = ª<br>
 * &laquo;  = «<br>
 * &not;    = ¬<br>
 * &shy;    = ­<br>
 * &reg;    = ®<br>
 * &macr;   = ¯<br>
 * &deg;    = °<br>
 * &plusmn; = ±<br>
 * &sup2;   = ²<br>
 * &sup3;   = ³<br>
 * &acute;  = ´<br>
 * &micro;  = µ<br>
 * &para;   = ¶<br>
 * &middot; = ·<br>
 * &cedil;  = ¸<br>
 * &sup1;   = ¹<br>
 * &ordm;   = º<br>
 * &raquo;  = »<br>
 * &frac14; = ¼<br>
 * &frac12; = ½<br>
 * &frac34; = ¾<br>
 * &iquest; = ¿<br>
 * &Agrave; = À<br>
 * &Aacute; = Á<br>
 * &Acirc;  = Â<br>
 * &Atilde; = Ã<br>
 * &Auml;   = Ä<br>
 * &Aring;  = Å<br>
 * &AElig;  = Æ<br>
 * &Ccedil; = Ç<br>
 * &Egrave; = È<br>
 * &Eacute; = É<br>
 * &Ecirc;  = Ê<br>
 * &Euml;   = Ë<br>
 * &Igrave; = Ì<br>
 * &Iacute; = Í<br>
 * &Icirc;  = Î<br>
 * &Iuml;   = Ï<br>
 * &ETH;    = Ð<br>
 * &Ntilde; = Ñ<br>
 * &Ograve; = Ò<br>
 * &Oacute; = Ó<br>
 * &Ocirc;  = Ô<br>
 * &Otilde; = Õ<br>
 * &Ouml;   = Ö<br>
 * &times;  = ×<br>
 * &Oslash; = Ø<br>
 * &Ugrave; = Ù<br>
 * &Uacute; = Ú<br>
 * &Ucirc;  = Û<br>
 * &Uuml;   = Ü<br>
 * &Yacute; = Ý<br>
 * &THORN;  = Þ<br>
 * &szlig;  = ß<br>
 * &agrave; = à<br>
 * &aacute; = á<br>
 * &acirc;  = â<br>
 * &atilde; = ã<br>
 * &auml;   = ä<br>
 * &aring;  = å<br>
 * &aelig;  = æ<br>
 * &ccedil; = ç<br>
 * &egrave; = è<br>
 * &eacute; = é<br>
 * &ecirc;  = ê<br>
 * &euml;   = ë<br>
 * &igrave; = ì<br>
 * &iacute; = í<br>
 * &icirc;  = î<br>
 * &iuml;   = ï<br>
 * &eth;    = ð<br>
 * &ntilde; = ñ<br>
 * &ograve; = ò<br>
 * &oacute; = ó<br>
 * &ocirc;  = ô<br>
 * &otilde; = õ<br>
 * &ouml;   = ö<br>
 * &divide; = ÷<br>
 * &oslash; = ø<br>
 * &ugrave; = ù<br>
 * &uacute; = ú<br>
 * &ucirc;  = û<br>
 * &uuml;   = ü<br>
 * &yacute; = ý<br>
 * &thorn;  = þ<br>
 * &yuml;   = ÿ<br>
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
    getOut().setStatus(getIn().getStatus());
    getOut().setValue(formatHtmlString(getIn().getValue()));
  }
  
  public static final String formatHtmlString(String strInput)
  {
    strInput = replaceString(strInput, "&amp;", "&");
    strInput = replaceString(strInput, "&quot;", "\"");
    strInput = replaceString(strInput, "&lt;", "<");
    strInput = replaceString(strInput, "&gt;", ">");
    strInput = replaceString(strInput, "&euro;", "€");
    strInput = replaceString(strInput, "&sbquo;", "‚");
    strInput = replaceString(strInput, "&fnof;", "ƒ");
    strInput = replaceString(strInput, "&bdquo;", "„");
    strInput = replaceString(strInput, "&hellip;", "…");
    strInput = replaceString(strInput, "&dagger;", "†");
    strInput = replaceString(strInput, "&Dagger;", "‡");
    strInput = replaceString(strInput, "&circ;", "ˆ");
    strInput = replaceString(strInput, "&permil;", "‰");
    strInput = replaceString(strInput, "&Scaron;", "Š");
    strInput = replaceString(strInput, "&lsaquo;", "‹");
    strInput = replaceString(strInput, "&OElig;", "Œ");
    strInput = replaceString(strInput, "&lsquo;", "‘");
    strInput = replaceString(strInput, "&rsquo;", "’");
    strInput = replaceString(strInput, "&ldquo;", "“");
    strInput = replaceString(strInput, "&rdquo;", "”");
    strInput = replaceString(strInput, "&bull;", "•");
    strInput = replaceString(strInput, "&ndash;", "–");
    strInput = replaceString(strInput, "&mdash;", "—");
    strInput = replaceString(strInput, "&tilde;", "˜");
    strInput = replaceString(strInput, "&trade;", "™");
    strInput = replaceString(strInput, "&scaron;", "š");
    strInput = replaceString(strInput, "&rsaquo;", "›");
    strInput = replaceString(strInput, "&oelig;", "œ");
    strInput = replaceString(strInput, "&yuml;", "Ÿ");
    strInput = replaceString(strInput, "&nbsp;", "");
    strInput = replaceString(strInput, "&iexcl;", "¡");
    strInput = replaceString(strInput, "&cent;", "¢");
    strInput = replaceString(strInput, "&pound;", "£");
    strInput = replaceString(strInput, "&curren;", "¤");
    strInput = replaceString(strInput, "&yen;", "¥");
    strInput = replaceString(strInput, "&brvbar;", "¦");
    strInput = replaceString(strInput, "&sect;", "§");
    strInput = replaceString(strInput, "&uml;", "¨");
    strInput = replaceString(strInput, "&copy;", "©");
    strInput = replaceString(strInput, "&ordf;", "ª");
    strInput = replaceString(strInput, "&laquo;", "«");
    strInput = replaceString(strInput, "&not;", "¬");
    strInput = replaceString(strInput, "&shy;", "­");
    strInput = replaceString(strInput, "&reg;", "®");
    strInput = replaceString(strInput, "&macr;", "¯");
    strInput = replaceString(strInput, "&deg;", "°");
    strInput = replaceString(strInput, "&plusmn;", "±");
    strInput = replaceString(strInput, "&sup2;", "²");
    strInput = replaceString(strInput, "&sup3;", "³");
    strInput = replaceString(strInput, "&acute;", "´");
    strInput = replaceString(strInput, "&micro;", "µ");
    strInput = replaceString(strInput, "&para;", "¶");
    strInput = replaceString(strInput, "&middot;", "·");
    strInput = replaceString(strInput, "&cedil;", "¸");
    strInput = replaceString(strInput, "&sup1;", "¹");
    strInput = replaceString(strInput, "&ordm;", "º");
    strInput = replaceString(strInput, "&raquo;", "»");
    strInput = replaceString(strInput, "&frac14;", "¼");
    strInput = replaceString(strInput, "&frac12;", "½");
    strInput = replaceString(strInput, "&frac34;", "¾");
    strInput = replaceString(strInput, "&iquest;", "¿");
    strInput = replaceString(strInput, "&Agrave;", "À");
    strInput = replaceString(strInput, "&Aacute;", "Á");
    strInput = replaceString(strInput, "&Acirc;", "Â");
    strInput = replaceString(strInput, "&Atilde;", "Ã");
    strInput = replaceString(strInput, "&Auml;", "Ä");
    strInput = replaceString(strInput, "&Aring;", "Å");
    strInput = replaceString(strInput, "&AElig;", "Æ");
    strInput = replaceString(strInput, "&Ccedil;", "Ç");
    strInput = replaceString(strInput, "&Egrave;", "È");
    strInput = replaceString(strInput, "&Eacute;", "É");
    strInput = replaceString(strInput, "&Ecirc;", "Ê");
    strInput = replaceString(strInput, "&Euml;", "Ë");
    strInput = replaceString(strInput, "&Igrave;", "Ì");
    strInput = replaceString(strInput, "&Iacute;", "Í");
    strInput = replaceString(strInput, "&Icirc;", "Î");
    strInput = replaceString(strInput, "&Iuml;", "Ï");
    strInput = replaceString(strInput, "&ETH;", "Ð");
    strInput = replaceString(strInput, "&Ntilde;", "Ñ");
    strInput = replaceString(strInput, "&Ograve;", "Ò");
    strInput = replaceString(strInput, "&Oacute;", "Ó");
    strInput = replaceString(strInput, "&Ocirc;", "Ô");
    strInput = replaceString(strInput, "&Otilde;", "Õ");
    strInput = replaceString(strInput, "&Ouml;", "Ö");
    strInput = replaceString(strInput, "&times;", "×");
    strInput = replaceString(strInput, "&Oslash;", "Ø");
    strInput = replaceString(strInput, "&Ugrave;", "Ù");
    strInput = replaceString(strInput, "&Uacute;", "Ú");
    strInput = replaceString(strInput, "&Ucirc;", "Û");
    strInput = replaceString(strInput, "&Uuml;", "Ü");
    strInput = replaceString(strInput, "&Yacute;", "Ý");
    strInput = replaceString(strInput, "&THORN;", "Þ");
    strInput = replaceString(strInput, "&szlig;", "ß");
    strInput = replaceString(strInput, "&agrave;", "à");
    strInput = replaceString(strInput, "&aacute;", "á");
    strInput = replaceString(strInput, "&acirc;", "â");
    strInput = replaceString(strInput, "&atilde;", "ã");
    strInput = replaceString(strInput, "&auml;", "ä");
    strInput = replaceString(strInput, "&aring;", "å");
    strInput = replaceString(strInput, "&aelig;", "æ");
    strInput = replaceString(strInput, "&ccedil;", "ç");
    strInput = replaceString(strInput, "&egrave;", "è");
    strInput = replaceString(strInput, "&eacute;", "é");
    strInput = replaceString(strInput, "&ecirc;", "ê");
    strInput = replaceString(strInput, "&euml;", "ë");
    strInput = replaceString(strInput, "&igrave;", "ì");
    strInput = replaceString(strInput, "&iacute;", "í");
    strInput = replaceString(strInput, "&icirc;", "î");
    strInput = replaceString(strInput, "&iuml;", "ï");
    strInput = replaceString(strInput, "&eth;", "ð");
    strInput = replaceString(strInput, "&ntilde;", "ñ");
    strInput = replaceString(strInput, "&ograve;", "ò");
    strInput = replaceString(strInput, "&oacute;", "ó");
    strInput = replaceString(strInput, "&ocirc;", "ô");
    strInput = replaceString(strInput, "&otilde;", "õ");
    strInput = replaceString(strInput, "&ouml;", "ö");
    strInput = replaceString(strInput, "&divide;", "÷");
    strInput = replaceString(strInput, "&oslash;", "ø");
    strInput = replaceString(strInput, "&ugrave;", "ù");
    strInput = replaceString(strInput, "&uacute;", "ú");
    strInput = replaceString(strInput, "&ucirc;", "û");
    strInput = replaceString(strInput, "&uuml;", "ü");
    strInput = replaceString(strInput, "&yacute;", "ý");
    strInput = replaceString(strInput, "&thorn;", "þ");
    strInput = replaceString(strInput, "&yuml;", "ÿ");
    
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
