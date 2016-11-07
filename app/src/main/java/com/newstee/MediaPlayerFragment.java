package com.newstee;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.newstee.MusicService.MusicBinder;
import com.newstee.helper.InternetHelper;
import com.newstee.model.data.DataPost;
import com.newstee.model.data.News;
import com.newstee.model.data.NewsLab;
import com.newstee.model.data.UserLab;
import com.newstee.network.FactoryApi;
import com.newstee.network.interfaces.NewsTeeApiInterface;
import com.newstee.utils.DisplayImageLoaderOptions;
import com.newstee.utils.MPUtilities;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sromku.simple.fb.SimpleFacebook;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKShareDialog;
import com.vk.sdk.dialogs.VKShareDialogBuilder;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arnold on 17.02.2016.
 */
public class MediaPlayerFragment extends Fragment implements  SeekBar.OnSeekBarChangeListener {
    private final static String TAG = "MediaPlayerFragment";
        private final static String NEWS_URL = NewsTeeApiInterface.BASE_URL+"onenews.php?id=";
    private final static String TEST_HTML = "<html>\n" +
            "<head>\n" +
            "\t<base href=\"https://shop.football.kharkov.ua/\"/>\n" +
            "\t<title>Мяч для футбола Select Brilliant Super</title>\n" +
            "\t\n" +
            "\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
            "\t<meta name=\"description\" content=\"Мяч для футбола Select Brilliant Super  одобрен международной федерацией футбола и имеет соответствующий сертификат (FIFA Approved), позволяющий проводить этим мячом крупные международные и местные соревнования.\" />\n" +
            "\t<meta name=\"keywords\"    content=\"Мяч для футбола Select Brilliant Super, Select Sport, Мячи для футбола\" />\n" +
            "\t<meta name=\"viewport\" content=\"width=1024\"/>\n" +
            "\t\n" +
            "\t<link href=\"design/shop.football/css/style.css\" rel=\"stylesheet\" type=\"text/css\" media=\"screen\"/>\n" +
            "\t<link href=\"design/shop.football/images/favicon.ico\" rel=\"icon\"          type=\"image/x-icon\"/>\n" +
            "\t<link href=\"design/shop.football/images/favicon.ico\" rel=\"shortcut icon\" type=\"image/x-icon\"/>\n" +
            "\t\n" +
            "\t<script src=\"js/jquery/jquery.js\"  type=\"text/javascript\"></script>\n" +
            "\t\n" +
            "\t\t\n" +
            "\t<script type=\"text/javascript\" src=\"js/fancybox/jquery.fancybox-1.3.4.pack.js\"></script>\n" +
            "\t<link rel=\"stylesheet\" href=\"js/fancybox/jquery.fancybox-1.3.4.css\" type=\"text/css\" media=\"screen\" />\n" +
            "        <script type=\"text/javascript\" src=\"design/shop.football/js/3dswift.js\"></script>\n" +
            "\t<script type=\"text/javascript\" src=\"js/ctrlnavigate.js\"></script>           \n" +
            "\t\n" +
            "        <script src=\"//code.jquery.com/ui/1.10.3/jquery-ui.js\"></script>\n" +
            "\t<script src=\"design/shop.football/js/ajax_cart.js\"></script>\n" +
            "\t\n" +
            "\t<script src=\"/js/baloon/js/baloon.js\" type=\"text/javascript\"></script>\n" +
            "\t<link   href=\"/js/baloon/css/baloon.css\" rel=\"stylesheet\" type=\"text/css\" /> \n" +
            "        \n" +
            "\t<script src=\"/js/jquery.selectBoxIt.min.js\" type=\"text/javascript\"></script>\n" +
            "\n" +
            "\t\n" +
            "\t\n" +
            "\t<script src=\"js/autocomplete/jquery.autocomplete-min.js\" type=\"text/javascript\"></script>\n" +
            "\t<style>\n" +
            "\t.autocomplete-w1 { position:absolute; top:0px; left:0px; margin:6px 0 0 6px; /* IE6 fix: */ _background:none; _margin:1px 0 0 0; }\n" +
            "\t.autocomplete { border:1px solid #999; background:#FFF; cursor:default; text-align:left; overflow-x:auto;  overflow-y: auto; margin:-6px 6px 6px -6px; /* IE6 specific: */ _height:350px;  _margin:0; _overflow-x:hidden; }\n" +
            "\t.autocomplete .selected { background:#F0F0F0; }\n" +
            "\t.autocomplete div { padding:2px 5px; white-space:nowrap; }\n" +
            "\t.autocomplete strong { font-weight:normal; color:#3399FF; }\n" +
            "\t</style>\t\n" +
            "\t<script>\n" +
            "\t$(function() {\n" +
            "                //Меняем дропдаун на стилизованный\n" +
            "\t\t$(\"select\").selectBoxIt();\n" +
            "                \n" +
            "                $(\"select[name='variant']\").on('change', function(event){\n" +
            "                  $(this).find(\"option[value='\" + $(this).val() + \"']\").attr('selected', 'true');\n" +
            "                  $(this).find(\"option[value='\" + $(this).val() + \"']\").siblings().removeAttr('selected');\n" +
            "                  $(\"input[name='variant'][type='hidden']\").val($(this).val());\n" +
            "                  console.log($(this).val());\n" +
            "                });\n" +
            "\t\t\n" +
            "\n" +
            "\t\t//  Автозаполнитель поиска\n" +
            "\t\t$(\".input_search\").autocomplete({\n" +
            "\t\t\tserviceUrl:'ajax/search_products.php',\n" +
            "\t\t\tminChars:1,\n" +
            "\t\t\tnoCache: false, \n" +
            "\t\t\tonSelect:\n" +
            "\t\t\t\tfunction(value, data){\n" +
            "\t\t\t\t\t $(\".input_search\").closest('form').submit();\n" +
            "\t\t\t\t},\n" +
            "\t\t\tfnFormatResult:\n" +
            "\t\t\t\tfunction(value, data, currentValue){\n" +
            "\t\t\t\t\tvar reEscape = new RegExp('(\\\\' + ['/', '.', '*', '+', '?', '|', '(', ')', '[', ']', '{', '}', '\\\\'].join('|\\\\') + ')', 'g');\n" +
            "\t\t\t\t\tvar pattern = '(' + currentValue.replace(reEscape, '\\\\$1') + ')';\n" +
            "\t  \t\t\t\treturn (data.image?\"<img align=absmiddle src='\"+data.image+\"'> \":'') + value.replace(new RegExp(pattern, 'gi'), '<strong>$1<\\/strong>');\n" +
            "\t\t\t\t}\t\n" +
            "\t\t});\n" +
            "\t});\n" +
            "\t</script>\n" +
            "        <script type=\"text/javascript\">\n" +
            "\n" +
            "  var _gaq = _gaq || [];\n" +
            "  _gaq.push(['_setAccount', 'UA-2028404-9']);\n" +
            "  _gaq.push(['_trackPageview']);\n" +
            "\n" +
            "  (function() {\n" +
            "    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n" +
            "    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n" +
            "    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n" +
            "  })();\n" +
            "  \n" +
            "\n" +
            "</script>\n" +
            "\t\n" +
            "\t\t\n" +
            "\t\t\t\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "\n" +
            "\t<!-- Верхняя строка -->\n" +
            "\t<div id=\"top_background\">\n" +
            "\t<div id=\"top\">\n" +
            "\t\n" +
            "\t\t<!-- Меню -->\n" +
            "\t\t<ul id=\"menu\">\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li >\n" +
            "\t\t\t\t\t<a data-page=\"127\" href=\"\">Каталог</a>\n" +
            "\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li >\n" +
            "\t\t\t\t\t<a data-page=\"121\" href=\"oplata\">Оплата</a>\n" +
            "\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li >\n" +
            "\t\t\t\t\t<a data-page=\"122\" href=\"delivery\">Доставка</a>\n" +
            "\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li >\n" +
            "\t\t\t\t\t<a data-page=\"161\" href=\"Справка\">Справка</a>\n" +
            "\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li >\n" +
            "\t\t\t\t\t<a data-page=\"163\" href=\"galereya\">Галерея</a>\n" +
            "\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li >\n" +
            "\t\t\t\t\t<a data-page=\"164\" href=\"blog\">Блог</a>\n" +
            "\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li >\n" +
            "\t\t\t\t\t<a data-page=\"123\" href=\"contacts\">Контакты</a>\n" +
            "\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t<!-- Меню (The End) -->\n" +
            "\t\n" +
            "\t\t<!-- Корзина -->\n" +
            "\t\t<div id=\"cart_informer\">\n" +
            "\t\t\t\n" +
            "\tКорзина пуста\n" +
            "\t\t</div>\n" +
            "\t\t<!-- Корзина (The End)-->\n" +
            "\n" +
            "\t\t<!-- Вход пользователя -->\n" +
            "\t\t<div id=\"account\">\n" +
            "\t\t\t\t\t\t\t<a id=\"register\" href=\"user/register\">Регистрация</a>\n" +
            "\t\t\t\t<a id=\"login\" href=\"user/login\">Вход</a>\n" +
            "\t\t\t\t\t</div>\n" +
            "\t\t<!-- Вход пользователя (The End)-->\n" +
            "\n" +
            "\t</div>\n" +
            "\t</div>\n" +
            "\t<!-- Верхняя строка (The End)-->\n" +
            "\t\n" +
            "\t\n" +
            "\t<!-- Шапка -->\n" +
            "\t<div id=\"header\">\n" +
            "\t\t<div id=\"logo\">\n" +
            "\t\t\t<a href=\"/\"><img src=\"design/shop.football/images/vector_logo_shop_final.png\" title=\"Футбольный магазин\" alt=\"Футбольный магазин\"/></a>\n" +
            "\t\t</div>\t\n" +
            "\t\t<div id=\"contact\">\n" +
            "\t\t\t<div id=\"address\">Харьков, ул. Полевая 44 (2 этаж)</div>\n" +
            "                        <span id=\"phone\">(057)737-95-65, (050)144-58-01, (098)703-444-8</span>\n" +
            "\t\t</div>\t\n" +
            "\t</div>\n" +
            "\t<!-- Шапка (The End)--> \n" +
            "\n" +
            "\n" +
            "\t<!-- Вся страница --> \n" +
            "\t<div id=\"main\">\n" +
            "\t\n" +
            "\t\t<!-- Основная часть --> \n" +
            "\t\t<div id=\"content\">\n" +
            "\t\t\t<!-- Хлебные крошки /-->\n" +
            "<div id=\"path\">\n" +
            "\t<a href=\"./\">Главная</a>\n" +
            "\t\t→ <a href=\"catalog/Футбольные_мячи\">Футбольные мячи</a>\n" +
            "\t\t→ <a href=\"catalog/Мячи_для_футбола\">Мячи для футбола</a>\n" +
            "\t\t\t→ <a href=\"catalog/Мячи_для_футбола/Select_Sport\">Select Sport</a>\n" +
            "\t\t→  Мяч для футбола Select Brilliant Super                \n" +
            "</div>\n" +
            "<!-- Хлебные крошки #End /-->\n" +
            "\n" +
            "<h1 data-product=\"1195\">Мяч для футбола Select Brilliant Super</h1>\n" +
            "\n" +
            "<div class=\"product\">\n" +
            "\n" +
            "\t<!-- Большое фото -->\n" +
            "\t\t<div class=\"image\">\n" +
            "\t\t<a href=\"https://shop.football.kharkov.ua/files/products/FnpH88R9oAAd0uBvVsdTJi0m6T1_u6tNad5IVRKbOWA.800x600w.jpg?02b4890e5cf788ae54fd404fad45cad6\" class=\"zoom\" data-rel=\"group\"><img src=\"https://shop.football.kharkov.ua/files/products/FnpH88R9oAAd0uBvVsdTJi0m6T1_u6tNad5IVRKbOWA.300x300.jpg?a07a970be5153fba9c4fadd8f0d0755f\" alt=\"\" /></a>\n" +
            "\t</div>\n" +
            "\t\t<!-- Большое фото (The End)-->\n" +
            "\n" +
            "\t<!-- Описание товара -->\n" +
            "\t<div class=\"description\">\n" +
            "\t\t\t<!-- Выбор варианта товара -->\n" +
            "\t\t<form class=\"variants\" action=\"/cart\">\n" +
            "\t\t\t<table>\n" +
            "\t\t\t<tr class=\"variant\">\n" +
            "\t\t\t\t<td>\n" +
            "\t\t\t\t                                  \n" +
            "                                 <span class=\"single_price\"  style=\"display: block;margin-bottom: 5px;float: left;margin-right: 10px;\">2 040,00 грн</span>\n" +
            "\t\t\t\t                                  <input type=\"hidden\" name=\"variant\" value=\"13510\" /> \t\n" +
            "                                 <div style=\"clear:both;\"></div>\n" +
            "                                 <select name=\"variant\">\n" +
            "                                                                          <option value=\"13510\" id=\"discounted_13510\"></option>\n" +
            "                                           \n" +
            "                                   </select> \n" +
            "\n" +
            "                                 \n" +
            "\t\t\t</tr>\n" +
            "\t\t\t</table>\n" +
            "                                                \n" +
            "\t\t\t<input type=\"submit\" class=\"button\" value=\"в корзину\" data-result-text=\"добавлено\"/>\n" +
            "\t\t</form>\n" +
            "\t\t<!-- Выбор варианта товара (The End) -->\t\t\t\n" +
            "         <div style=\"clear:both;\"></div>\t\n" +
            "\t\t<p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\" id=\"docs-internal-guid-6a484bd7-9fa8-7e23-b65c-b5ec21e30fcd\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\">Специально изготовленная подоснова покрышки мяча <strong>Select Brilliant Super</strong> из тонко сплетенного хлопка и полиэстера обеспечивает мячу новую чувствительность и игровой контроль. </span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\">Ультра- тонкий и очень эластичный микрофибр обеспечивает ощущение, что мяч значительно мягче и его легче ударить, чем раньше. </span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\">Снаружи <strong>Brilliant</strong> оснащен совершенно новой и чрезвычайно прочный слоем материала PU (полиуретан) с совершенно новой текстурой поверхности.</span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\">Красивый дизайн с переливающейся текстурированной поверхностью.</span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\">Изготовлен из дорогой и износостойкой синтетической кожи - полиуретана, толщиной 1,9 мм.</span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\">Подкладочные слои пропитаны водооталкивающим составом, что препятствует намоканию и увеличению веса при игре в сырую погоду.</span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\">Идеально сбалансированная латексная, безшовная камера с двойным бутиловым ниппелем делает мяч контролируемым, с предсказуемым отскоком, правильной траекторией полета и мгновенным восстановлением формы, после удара.</span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><strong><span style=\"font-size: 14.6667px; font-family: Arial; color: #000000; background-color: transparent; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></strong></p><p><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><strong>Мяч для футбола Select Brilliant Super</strong> одобрен международной федерацией футбола и имеет соответствующий сертификат (FIFA Approved), позволяющий проводить этим мячом крупные международные и местные соревнования.</span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\">Футбольный мяч <strong>Select Brillant Super</strong> подходит для игры на натуральном и искусственном поле в любую погоду.</span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><strong>Использование:</strong> Клубные матча и тренировки</span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><strong>Камера:</strong> латексная, патентованная технология \"Zero-wing\" с двойным бутиловым клапаном.</span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><strong>Материал:</strong> MFPUS 2000 - износостойкий полиуретан c волокнами микрофибры и текстурированной поверхностью, сводит к минимуму сопротивление воздуха для стабильного полета мяча.</span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><strong>Размер:</strong> 5.</span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><strong>Вес:</strong> 435 гр.</span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><strong><span style=\"font-size: 14.6667px; font-family: Arial; color: #000000; background-color: transparent; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></strong></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><strong>Производитель:</strong> Пакистан</span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><strong>Компания:</strong> Select (Дания)</span></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><strong><span style=\"font-size: 14.6667px; font-family: Arial; color: #000000; background-color: transparent; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></strong></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><strong>Гарантия:</strong> 1 месяц (на швы и клапан)</span></p><p></p><p dir=\"ltr\" style=\"line-height: 1.38; margin-top: 0pt; margin-bottom: 0pt;\"><span style=\"font-size: 14.666666666666666px; font-family: Arial; color: #000000; background-color: transparent; font-weight: 400; font-style: normal; font-variant: normal; text-decoration: none; vertical-align: baseline;\"><br /></span></p>\n" +
            "\t\t\n" +
            "\t</div>\n" +
            "\t<!-- Описание товара (The End)-->\n" +
            "\n" +
            "\t<!-- Дополнительные фото продукта -->\n" +
            "\t\t<div class=\"images\">\n" +
            "\t\t\t\t\t<a href=\"https://shop.football.kharkov.ua/files/products/A6C90ql7CwBv7pgTNHTl-I8tchjGUGEJAQIjH7i1gkg.800x600w.jpg?639d92a894d96c5b795b5257194506f6\" class=\"zoom\" data-rel=\"group\"><img src=\"https://shop.football.kharkov.ua/files/products/A6C90ql7CwBv7pgTNHTl-I8tchjGUGEJAQIjH7i1gkg.95x95.jpg?6facff887c3ef94e908a5f1e8d9fdd96\" alt=\"Мяч для футбола Select Brilliant Super\" /></a>\n" +
            "\t\t\t\t\t<a href=\"https://shop.football.kharkov.ua/files/products/TkoxdjKqQFxNdyjTu4K6OSaYzspt0ExO56EJc3M8r-M.800x600w.jpg?bfde1c36aa9a745c108855e788c40923\" class=\"zoom\" data-rel=\"group\"><img src=\"https://shop.football.kharkov.ua/files/products/TkoxdjKqQFxNdyjTu4K6OSaYzspt0ExO56EJc3M8r-M.95x95.jpg?8eea48479e69d6de645573fd92c2b3d0\" alt=\"Мяч для футбола Select Brilliant Super\" /></a>\n" +
            "\t\t\t</div>\n" +
            "\t\t<!-- Дополнительные фото продукта (The End)-->\n" +
            "\n" +
            "\t\n" +
            "\t\n" +
            "\t<!-- Соседние товары /-->\n" +
            "\t<div id=\"back_forward\">\n" +
            "\t\t\t\t\t←&nbsp;<a class=\"prev_page_link\" href=\"products/myach-futbolnyj-select-velocity-ims-2015\">Мяч футбольный SELECT Velocity IMS 2015</a>\n" +
            "\t\t\t\t\t</div>\n" +
            "\t\n" +
            "</div>\n" +
            "<!-- Описание товара (The End)-->\n" +
            "\n" +
            "<!-- Комментарии -->\n" +
            "<div id=\"comments\">\n" +
            "\n" +
            "\t<h2>Комментарии</h2>\n" +
            "\t\n" +
            "\t\t<p>\n" +
            "\t\tПока нет комментариев\n" +
            "\t</p>\n" +
            "\t\t\n" +
            "\t<!--Форма отправления комментария-->\t\n" +
            "\t<form class=\"comment_form\" method=\"post\">\n" +
            "\t\t<h2>Написать комментарий</h2>\n" +
            "\t\t\t\t<textarea class=\"comment_textarea\" id=\"comment_text\" name=\"text\" data-format=\".+\" data-notice=\"Введите комментарий\"></textarea><br />\n" +
            "\t\t<div>\n" +
            "\t\t<label for=\"comment_name\">Имя</label>\n" +
            "\t\t<input class=\"input_name\" type=\"text\" id=\"comment_name\" name=\"name\" value=\"\" data-format=\".+\" data-notice=\"Введите имя\"/><br />\n" +
            "\n" +
            "\t\t<input class=\"button\" type=\"submit\" name=\"comment\" value=\"Отправить\" />\n" +
            "\t\t\n" +
            "\t\t<label for=\"comment_captcha\">Число</label>\n" +
            "\t\t<div class=\"captcha\"><img src=\"captcha/image.php?2431\" alt='captcha'/></div> \n" +
            "\t\t<input class=\"input_captcha\" id=\"comment_captcha\" type=\"text\" name=\"captcha_code\" value=\"\" data-format=\"\\d\\d\\d\\d\" data-notice=\"Введите капчу\"/>\n" +
            "\t\t\n" +
            "\t\t</div>\n" +
            "\t</form>\n" +
            "\t<!--Форма отправления комментария (The End)-->\n" +
            "\t\n" +
            "</div>\n" +
            "<!-- Комментарии (The End) -->\n" +
            "\n" +
            "\n" +
            "<script>\n" +
            "$(function() {\n" +
            "\t// Раскраска строк характеристик\n" +
            "\t$(\".features li:even\").addClass('even');\n" +
            "\n" +
            "\t// Зум картинок\n" +
            "\t$(\"a.zoom\").fancybox({ 'hideOnContentClick' : true });\n" +
            "        $(\"#size-table\").click(function() {\n" +
            "\t\t$.fancybox({\n" +
            "\t\t\t'padding'\t\t: 0,\n" +
            "\t\t\t'href'\t\t\t: 'https://shop.football.kharkov.ua/design/shop.football/images/Select_Sport__men.png'\n" +
            "\t\t});\n" +
            "                return false;\n" +
            "    });\n" +
            "     });\n" +
            "</script>\n" +
            "\n" +
            " \n" +
            "\n" +
            "\n" +
            "\n" +
            "\t\t</div>\n" +
            "\t\t<!-- Основная часть (The End) --> \n" +
            "\n" +
            "\t\t<div id=\"left\">\n" +
            "\n" +
            "\t\t\t<!-- Поиск-->\n" +
            "\t\t\t<div id=\"search\">\n" +
            "\t\t\t\t<form action=\"products\">\n" +
            "\t\t\t\t\t<input class=\"input_search\" type=\"text\" name=\"keyword\" value=\"\" placeholder=\"Поиск товара\"/>\n" +
            "\t\t\t\t\t<input class=\"button_search\" value=\"\" type=\"submit\" />\n" +
            "\t\t\t\t</form>\n" +
            "\t\t\t</div>\n" +
            "\t\t\t<!-- Поиск (The End)-->\n" +
            "\n" +
            "\t\t\t\n" +
            "\t\t\t<!-- Меню каталога -->\n" +
            "\t\t\t<div id=\"catalog_menu\">\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t<ul>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Акции\" data-category=\"87\">Акции и распродажи</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Футбольные_мячи\" data-category=\"59\">Футбольные мячи</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<ul>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a class=\"selected\" href=\"catalog/Мячи_для_футбола\" data-category=\"60\">Мячи для футбола</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Мячи_для_минифутбола\" data-category=\"61\">Мячи для мини-футбола</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/myachi-dlya-ulichnogo-futbola\" data-category=\"122\">Мячи для уличного футбола</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/detskie-myachi\" data-category=\"120\">Детские мячи</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/-myachi-dlya-volejbola-\" data-category=\"125\"> Мячи для волейбола </a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Аксессуары_для_мячей\" data-category=\"78\">Аксессуары для мячей</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Футбольная_форма\" data-category=\"70\">Футбольная форма</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<ul>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Комплекты_форм_и_футболки\" data-category=\"72\">Комплекты форм</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Футболки\" data-category=\"71\">Футболки</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Шорты\" data-category=\"73\">Шорты</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/klubnaya-forma\" data-category=\"114\">Клубная форма</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/forma-sbornyh\" data-category=\"116\">Форма сборных</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Футбольные_гетры\" data-category=\"62\">Футбольные гетры</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Футбольные_щитки\" data-category=\"65\">Футбольные щитки</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Вратарские_аксессуары\" data-category=\"63\">Вратарские аксессуары</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<ul>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Вратарские_кофты\" data-category=\"85\">Вратарские кофты</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Вратарские_перчатки\" data-category=\"64\">Вратарские перчатки</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Вратарские_штаны_шорты\" data-category=\"76\">Вратарские штаны/шорты</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Футбольная_обувь\" data-category=\"66\">Футбольная обувь</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<ul>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Футбольные_бутсы\" data-category=\"67\">Футбольные бутсы</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Сороконожки\" data-category=\"68\">Сороконожки</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Обувь_для_минифутбола\" data-category=\"69\">Обувь для мини-футбола</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Медицина\" data-category=\"74\">Медицина</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<ul>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Заморозки_крема\" data-category=\"108\">Заморозки, крема</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Для_голеностопа\" data-category=\"107\">Для голеностопа</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Для_колена\" data-category=\"109\">Для колена</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Для_запястья\" data-category=\"112\">Для запястья</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Для_тренировок\" data-category=\"77\">Для тренировок</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<ul>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Манишки\" data-category=\"95\">Манишки</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Термобелье\" data-category=\"86\">Термобелье</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/аксессуары-и-оборудование\" data-category=\"83\">Аксессуары и оборудование</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Сумки_и_рюкзаки\" data-category=\"90\">Сумки и рюкзаки</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Футбольные_сувениры\" data-category=\"91\">Футбольные сувениры</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<ul>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Кружки\" data-category=\"92\">Кружки</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Шевроны\" data-category=\"94\">Шевроны</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/На_каждый_день\" data-category=\"97\">На каждый день</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<ul>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Одежда\" data-category=\"98\">Одежда</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Обувь\" data-category=\"99\">Обувь</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Аксессуары\" data-category=\"100\">Аксессуары</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Судейская_амуниция\" data-category=\"101\">Судейская амуниция</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Детская_амуниция\" data-category=\"103\">Детская амуниция</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<ul>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Детская_футбольная_форма\" data-category=\"104\">Детская футбольная форма</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Детская_футбольная_обувь\" data-category=\"105\">Детская футбольная обувь</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/Детские_футбольные_аксессуары\" data-category=\"106\">Детские футбольные аксессуары</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/detskie-kurtki-shtany-kostyumy\" data-category=\"118\">Детские куртки, штаны, костюмы</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/detskaya-forma-klubov\" data-category=\"115\">Детская форма клубов</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t<a  href=\"catalog/detskaya-forma-sbornyh\" data-category=\"119\">Детская форма сборных</a>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t\t\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t\t\n" +
            "\t\t\t</div>\n" +
            "\t\t\t<!-- Меню каталога (The End)-->\t\t\n" +
            "\t\n" +
            "\t\t\t\n" +
            "\t\t\t<!-- Все бренды -->\n" +
            "\t\t\t\n" +
            "\t\t\t\t\t\t<div id=\"all_brands\">\n" +
            "\t\t\t\t<h2>Все бренды:</h2>\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/adidas\">Adidas</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/diadora\">Diadora</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/Joma\">Joma</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/k-sector\">K-sector</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/Liga_Sport\">Liga Sport</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/Medisport\">Medisport</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/mikasa\">MIKASA</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/mizuno\">Mizuno</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/Mueller\">Mueller</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/Nike\">Nike</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/puma\">Puma</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/Select_Sport\">Select Sport</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/SVA\">SVA</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/swift\">Swift</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/uhlsport\">Uhlsport</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\t<a href=\"brands/Winner\">Winner</a>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
            "\t\t\t\t\t\t<!-- Все бренды (The End)-->\n" +
            "\n" +
            "\t\t\t<!-- Выбор валюты -->\n" +
            "\t\t\t\t\t\t<!-- Выбор валюты (The End) -->\t\n" +
            "\n" +
            "\t\t\t\n" +
            "\t\t\t<!-- Просмотренные товары -->\n" +
            "\t\t\t\n" +
            "\t\t\t\t\t\t\n" +
            "\t\t\t\t<h2>Вы просматривали:</h2>\n" +
            "\t\t\t\t<ul id=\"browsed_products\">\n" +
            "\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t<a href=\"products/myach-dlya-futbola-select-brilliant-super\"><img src=\"https://shop.football.kharkov.ua/files/products/FnpH88R9oAAd0uBvVsdTJi0m6T1_u6tNad5IVRKbOWA.50x50.jpg?31075166591f03c4045d312d15092b09\" alt=\"Мяч для футбола Select Brilliant Super\" title=\"Мяч для футбола Select Brilliant Super\"></a>\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t\t<li>\n" +
            "\t\t\t\t\t<a href=\"products/futbolka_joma_victory\"><img src=\"https://shop.football.kharkov.ua/files/products/20121217133109.1239.98.91.50x50.jpg?1d4db29a9ff7bcdf4bd61b37118dab42\" alt=\"Футболка Joma Victory\" title=\"Футболка Joma Victory\"></a>\n" +
            "\t\t\t\t\t</li>\n" +
            "\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t\t\t\t\t<!-- Просмотренные товары (The End)-->\n" +
            "\t\t\t\n" +
            "\t\t\t\n" +
            "\t\t\t<!-- Меню блога -->\n" +
            "\t\t\t\n" +
            "\t\t\t\t\t\t<div id=\"blog_menu\">\n" +
            "\t\t\t\t<h2>Новые записи в <a href=\"blog\">блоге</a></h2>\n" +
            "\t\t\t\t\t\t\t\t<ul>\n" +
            "\t\t\t\t\t<li data-post=\"13\">17.02.2014 <a href=\"blog/kineziologicheskoe-tejpirovanie-travmoopasno-tolko-dlya-vashego-yazyka\">Кинезиологическое тейпирование: травмоопасно только для вашего языка</a></li>\n" +
            "\t\t\t\t</ul>\n" +
            "\t\t\t\t\t\t\t\t<ul>\n" +
            "\t\t\t\t\t<li data-post=\"11\">19.12.2013 <a href=\"blog/chto-podarit-futbolistu-ili-kak-skolotit-sostoyanie-na-noskah\">Что подарить футболисту или как сколотить состояние на носках?</a></li>\n" +
            "\t\t\t\t</ul>\n" +
            "\t\t\t\t\t\t\t\t<ul>\n" +
            "\t\t\t\t\t<li data-post=\"7\">02.05.2012 <a href=\"blog/Joma_Top_Flex_в_действии\">Joma Top Flex в действии!</a></li>\n" +
            "\t\t\t\t</ul>\n" +
            "\t\t\t\t\t\t\t\t<ul>\n" +
            "\t\t\t\t\t<li data-post=\"8\">04.10.2011 <a href=\"blog/Ремонт_футбольных_мячей\">Ремонт футбольных мячей</a></li>\n" +
            "\t\t\t\t</ul>\n" +
            "\t\t\t\t\t\t\t\t<ul>\n" +
            "\t\t\t\t\t<li data-post=\"6\">24.07.2011 <a href=\"blog/Коллекция_формы_Joma_для_Valencia_CF\">Коллекция формы Joma для Valencia C.F.</a></li>\n" +
            "\t\t\t\t</ul>\n" +
            "\t\t\t\t\t\t\t</div>\n" +
            "\t\t\t\t\t\t<!-- Меню блога  (The End) -->\n" +
            "\t\t\t\n" +
            "\t\t</div>\t\t\t\n" +
            "\n" +
            "\t</div>\n" +
            "\t<!-- Вся страница (The End)--> \n" +
            "\t\n" +
            "\t<!-- Футер -->\n" +
            "\t<div id=\"footer\">\n" +
            "\t\t2009-2016 &copy;\n" +
            "\t</div>\n" +
            "\t<!-- Футер (The End)--> \n" +
            "\t\n" +
            "</body>\n" +
            "</html>";
    private int newSongValue = -1;
    private boolean playingValue = false;
    private SimpleFacebook mSimpleFacebook;
    private ImageButton btnPlay;
    private MediaConsumer mediaConsumer;
  //  private ImageButton btnForward;
  //  private ImageButton btnBackward;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnPlaylist;
    private ImageButton btnLike;
    private ImageButton btnShare;
    private ImageButton btnComment;
    private ImageView songImageView;
    private ImageView adContainerImageView;
    private SeekBar songProgressBar;
    private SeekBar volumeSeekBar;
    private TextView newsDate;
    private TextView songTitleLabel;
    private WebView songContent;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
        private AlertDialog shareDialog;
    private MPUtilities utils;
    private Handler mHandler = new Handler();
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    //song catalogue variables
    private List<News> mNewsList;
    //service
    private MusicService musicSrv;
    private Intent playIntent;
    //binding
    private boolean musicBound=false;
    //controller

    CoordinatorLayout.Behavior behavior;




        //activity and playback pause flags
    private boolean paused=false, playbackPaused=false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewsList = new ArrayList<News>();
        mSimpleFacebook = SimpleFacebook.getInstance(getActivity());
        getSongList();

        Log.d(TAG, "******onCreate*****");

    }



    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "******onResume*****");
        updateMediaPlayer();
    }

    /*public void updateMPNews() {
        if (musicSrv != null) {
            if (musicSrv.isPlaying()) {
                btnPlay.setImageResource(R.drawable.ic_media_pause);
            } else {
                btnPlay.setImageResource(R.drawable.ic_media_play);
            }
            songTitleLabel.setText(musicSrv.getSongTitle());
            songContent.setText(musicSrv.getSongContent());
            imageLoader.displayImage(musicSrv.getNewsPictureUrl(),songImageView, DisplayImageLoaderOptions.getInstance());
        }
    }*/


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                res.saveTokenToSharedPreferences(getActivity(), VKAccessToken.ACCESS_TOKEN);
                // User passed Authorization
            }

            @Override
            public void onError(VKError error) {
                // User didn't pass Authorization
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        if(requestCode == PlaylistActivity.REQUEST_CODE)
        {
            Log.d(TAG, "******Result*****");
            if (data == null) {
                Log.d(TAG, "****** data = null *****");

                return;
            }
            Log.d(TAG, "****** data != null *****");
            boolean exit = data.getBooleanExtra(PlaylistActivity.DATA_EXTRA_EXIT, false);
            if (exit) {
                getActivity().finish();
            }
            String newsId = data.getStringExtra(PlaylistActivity.DATA_EXTRA_NEWS_ID);
            if(newsId !=null)
            {
                playSong(newsId);
            }
        }
    }

  /*  private final VKSdkListener vkSdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }
        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(vkScope, true, false);
        }
        @Override
        public void onAccessDenied(VKError authorizationError) {
            new AlertDialog.Builder(SocialNetworkActivity.this)
                    .setMessage(authorizationError.errorMessage)
                    .show();
        }
        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            newToken.saveTokenToSharedPreferences(getApplicationContext(), vkTokenKey);
        }
    };*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "******onCreateView*****");
        //retrieve catalogue view
        View root = inflater.inflate(R.layout.media_conroller_coordinator,container, false );
        btnPlay = (ImageButton) root.findViewById(R.id.pause);
        btnLike = (ImageButton)root.findViewById(R.id.like_imageButton);
       btnShare = (ImageButton)root.findViewById(R.id.share_imageButton);
        btnComment = (ImageButton)root.findViewById(R.id.comment_imageButton);
        adContainerImageView = (ImageView)root.findViewById(R.id.media_controller_ad_container);
        adContainerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InternetHelper.goToDeveloperSite(getActivity());
            }
        });
    //    btnForward = (ImageButton) root.findViewById(R.id.ffwd);
    //    btnBackward = (ImageButton) root.findViewById(R.id.rew);
        btnNext = (ImageButton) root.findViewById(R.id.next);
     //   Log.d("asff", btnForward.getWidth()+"  " + btnBackward.getHeight());
        btnPrevious = (ImageButton) root.findViewById(R.id.prev);
        btnPlaylist = (ImageButton) root.findViewById(R.id.play_list_imageButton);
        volumeSeekBar = (SeekBar)root.findViewById(R.id.volume_seekBar);
        songProgressBar = (SeekBar) root.findViewById(R.id.during_seekBar);
        songImageView = (ImageView)root.findViewById(R.id.canal_imageView);
        songContent = (WebView) root.findViewById(R.id.media_controller_new_content_textView);
        songTitleLabel = (TextView) root.findViewById(R.id.title_TextView);
        songCurrentDurationLabel = (TextView) root.findViewById(R.id.curent_time_TextView);
        songTotalDurationLabel = (TextView) root.findViewById(R.id.total_time_TextView);
        newsDate = (TextView)root.findViewById(R.id.date_TextView);
        utils = new MPUtilities();

btnShare.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (musicSrv == null) {
            return;
        }
        if (musicSrv.getIdNews() == null) {
            return;
        }
          final AlertDialog.Builder shareBuilder = new AlertDialog.Builder(getActivity());
          ;
          /*    final ArrayAdapter<ImageView> arrayAdapter = new ArrayAdapter<ImageView>(
                    getActivity(),
                    android.R.layout.select_dialog_singlechoice);
            ImageView imageVkView = new ImageButton(getActivity());
            imageVkView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageVkView.setImageResource(R.drawable.vkontakte_logo);
            arrayAdapter.add(imageVkView);
            ImageView imageFcbView = new ImageButton(getActivity());
            imageVkView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageVkView.setImageResource(R.drawable.facebook_logo);
            arrayAdapter.add(imageFcbView);*/
            shareBuilder.setIcon(R.drawable.ic_share_click);
            shareBuilder.setTitle(R.string.share);
            View shareLayout = getActivity().getLayoutInflater().inflate(R.layout.share_dialog, null);
            ImageButton shareVk = (ImageButton)shareLayout.findViewById(R.id.share_dialog_vkontakte);
            ImageButton shareFacebook = (ImageButton) shareLayout.findViewById(R.id.share_dialog_facebook);
            shareFacebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String link = NEWS_URL + musicSrv.getIdNews();
                        String title = musicSrv.getSongTitle();
                        String linkPicture = musicSrv.getNewsPictureUrl();
                        linkPicture =  InternetHelper.toCorrectLink(linkPicture);
                            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                                    .setContentTitle(title)
                                            // .setContentDescription(musicSrv.getSongContent())
                                    .setContentUrl(Uri.parse(link))
                                    .setImageUrl(Uri.parse(linkPicture))
                                    .build();
                            ShareDialog.show(getActivity(), shareLinkContent);
                            if (shareDialog != null) {
                                    shareDialog.dismiss();

                            }

                    }
            });
            shareVk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            String link = NEWS_URL + musicSrv.getIdNews();
                            String title = musicSrv.getSongTitle();
                            String linkPicture = musicSrv.getNewsPictureUrl();
                        linkPicture= InternetHelper.toCorrectLink(linkPicture);
                            vkontaktePublish(link, title, linkPicture);
                            if (shareDialog != null) {
                                    shareDialog.dismiss();

                            }

                    }
            });
            shareBuilder.setView(shareLayout);
           /* shareDialog.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
            });*/
            shareBuilder.setNegativeButton(R.string.btn_cancel,
                    new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                            }
                    });

            shareDialog = shareBuilder.create();
            shareDialog.show();
         /*   String content  = musicSrv.getSongContent();
            String link = "http://213.231.4.68/music-web/app/php/page_show_news.php?id=" + musicSrv.getIdNews();
            String title = musicSrv.getSongTitle();
            String linkPicture = musicSrv.getNewsPictureUrl();
            vkontaktePublish(content, link, title, linkPicture);*/

      /*  Feed feed = new Feed.Builder()
                .setName(musicSrv.getSongTitle())
                .setDescription(musicSrv.getSongContent())
                .setPicture(musicSrv.getNewsPictureUrl())
                .setLink("http://213.231.4.68/music-web/app/php/page_show_news.php?id="+musicSrv.getIdNews())
                .build();
        mSimpleFacebook.publish(feed, true,onPublishListener);*//*
       /* Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.parse(musicSrv.getNewsPictureUrl());
        sharingIntent.setType("image/png");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        startActivity(Intent.createChooser(sharingIntent, "Share image using"));*/
        }
});

btnLike.setOnClickListener(new View.OnClickListener() {

    @Override
    public void onClick(final View v) {
        if(musicSrv == null)
        {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.msg_error), Toast.LENGTH_LONG).show();

            return;
        }
        final String id = musicSrv.getIdNews();
        if(id == null)
        {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.msg_error), Toast.LENGTH_LONG).show();

            return;
        }
        if (!InternetHelper.getInstance(getActivity()).isOnline()) {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.check_internet_con), Toast.LENGTH_LONG).show();
            return;
        }

       Animation animation = AnimationUtils.loadAnimation(
               getActivity(), R.anim.skale_animation);
        if(!(UserLab.getInstance().likeNews(PlayList.getInstance().getNewsItem(id),getActivity())))
        {
            return;
        }
        News threadNews = NewsLab.getInstance().getNewsItem(id);
        News addedNews = UserLab.getInstance().getAddedNewsItem(id);
        boolean isAdded  = false;
        v.startAnimation(animation);
        if (UserLab.getInstance().isLikedNews(id)) {
            Log.d(TAG, "@@@@@@ liked");
            ((ImageButton) v).setImageResource(R.drawable.ic_is_liked);
            isAdded = true;
        } else {
            Log.d(TAG, "@@@@@@ removed");
            ((ImageButton) v).setImageResource(R.drawable.ic_like);
        }
        if(NewsLab.getInstance().getNews().contains(addedNews))
        {
            if(isAdded)
            {
                addedNews.addLike();
            }
            else
            {
                addedNews.removeLike();
            }


        }
        else if(threadNews != null && addedNews != null  )
        {
            if(isAdded)
            {
                addedNews.addLike();
                threadNews.addLike();
            }
            else
            {
                addedNews.removeLike();
                threadNews.removeLike();
            }
        }
        else if(addedNews != null)
        {
            if(isAdded)
            {
                addedNews.addLike();
            }
            else
            {
                addedNews.removeLike();
            }
        }
        else if(threadNews != null)
        {
            if(isAdded)
            {
                threadNews.addLike();
            }
            else
            {
                threadNews.removeLike();
            }
        }
        NewsTeeApiInterface nApi = FactoryApi.getInstance(getActivity());

        Call<DataPost> call = nApi.likeNews(id);
        call.enqueue(new Callback<DataPost>() {
            @Override
            public void onResponse(Call<DataPost> call, Response<DataPost> response) {
                if (response.body().getResult().equals(Constants.RESULT_SUCCESS)) {


                } else {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataPost> call, Throwable t) {

                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
});
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent = new Intent(getContext(), CommentActivity.class);
                News n = PlayList.getInstance().getCurrent();
                if(n == null || n.getId() == null)
                {
                    return;
                }
                commentIntent.putExtra(CommentActivity.ARG_NEWS_ID,n.getId());
                startActivity(commentIntent);
            }
        });
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicSrv == null) {
                    return;
                }
                musicSrv.setVolume(progress);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

                songProgressBar.setOnSeekBarChangeListener(this);
        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = getResources().getColor(R.color.colorPrimary, null);
        }
        else {
            color = getResources().getColor(R.color.colorPrimary);
        }
        songProgressBar.getProgressDrawable().setColorFilter(
               color, PorterDuff.Mode.SRC_IN);
        volumeSeekBar.getProgressDrawable().setColorFilter(
                color, PorterDuff.Mode.SRC_IN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            songProgressBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            volumeSeekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PlaylistActivity.class);
                startActivityForResult(i, PlaylistActivity.REQUEST_CODE);
            }
        });
       /* btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = musicSrv.getPosn();
                int totalPos = musicSrv.getDur();
                // check if seekForward time is lesser than song duration
                if (currentPosition + seekForwardTime <= totalPos) {
                    // forward song
                    musicSrv.seek(currentPosition + seekForwardTime);
                } else {
                    // forward to end position
                    musicSrv.seek(totalPos);
                }
            }
        });*/

          /*  btnBackward.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // get current song position
                            int currentPosition = musicSrv.getPosn();
                            // check if seekBackward time is greater than 0 sec
                            if(currentPosition - seekBackwardTime >= 0){
                                // forward song
                                musicSrv.seek(currentPosition - seekBackwardTime);
                            }else{
                                // backward to starting position
                                musicSrv.seek(0);
                            }

                }
            });*/
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next song is there or not
                if(musicSrv == null)
                {
                 return;
                }
                musicSrv.playNext();

            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next song is there or not
                if(musicSrv == null)
                {
                    return;
                }
                musicSrv.playPrev();

            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if(musicBound){
                    if(musicSrv != null)
                    {
                        if(musicSrv.isPlaying()){

                            musicSrv.pausePlayer();
                            btnPlay.setImageResource(R.drawable.ic_media_play);
                        }
                        else{
                            // Resume song
                            musicSrv.go();
                            // Changing button image to pause button
                            btnPlay.setImageResource(R.drawable.ic_media_pause);

                        }
                    }
                }
            }
        });

        //instantiate catalogue

        //get songs from device

        //sort alphabetically by title

        //create and set adapter
     /*   SongAdapter songAdt = new SongAdapter(getActivity(), songList);
       songView.setAdapter(songAdt);
        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                songPicked(view);
                                            }
                                        });

                //setup controller
                setController(root);*/

        return root;
    }
  /*      private int getScale(){
                Display display = ((WindowManager)getActivity(). getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                Double val = new Double(width)/new Double(PIC_WIDTH);
                val = val * 100d;
                return val.intValue();
        }*/



    public static String bbcode(String text) {
        String html = text;

        Map<String,String> bbMap = new HashMap<String , String>();

        bbMap.put("(\r\n|\r|\n|\n\r)", "<br/>");
        bbMap.put("\\[b\\](.+?)\\[/b\\]", "<strong>$1</strong>");
        bbMap.put("\\[i\\](.+?)\\[/i\\]", "<span style='font-style:italic;'>$1</span>");
        bbMap.put("\\[u\\](.+?)\\[/u\\]", "<span style='text-decoration:underline;'>$1</span>");
        bbMap.put("\\[h1\\](.+?)\\[/h1\\]", "<h1>$1</h1>");
        bbMap.put("\\[h2\\](.+?)\\[/h2\\]", "<h2>$1</h2>");
        bbMap.put("\\[h3\\](.+?)\\[/h3\\]", "<h3>$1</h3>");
        bbMap.put("\\[h4\\](.+?)\\[/h4\\]", "<h4>$1</h4>");
        bbMap.put("\\[h5\\](.+?)\\[/h5\\]", "<h5>$1</h5>");
        bbMap.put("\\[h6\\](.+?)\\[/h6\\]", "<h6>$1</h6>");
        bbMap.put("\\[quote\\](.+?)\\[/quote\\]", "<blockquote>$1</blockquote>");
        bbMap.put("\\[p\\](.+?)\\[/p\\]", "<p>$1</p>");
        bbMap.put("\\[p=(.+?),(.+?)\\](.+?)\\[/p\\]", "<p style='text-indent:$1px;line-height:$2%;'>$3</p>");
        bbMap.put("\\[center\\](.+?)\\[/center\\]", "<div align='center'>$1");
        bbMap.put("\\[align=(.+?)\\](.+?)\\[/align\\]", "<div align='$1'>$2");
        bbMap.put("\\[color=(.+?)\\](.+?)\\[/color\\]", "<span style='color:$1;'>$2</span>");
        bbMap.put("\\[size=(.+?)\\](.+?)\\[/size\\]", "<span style='font-size:$1;'>$2</span>");
        bbMap.put("\\[img\\](.+?)\\[/img\\]", "<img src='$1' />");
        bbMap.put("\\[img=(.+?),(.+?)\\](.+?)\\[/img\\]", "<img width='$1' height='$2' src='$3' />");
        bbMap.put("\\[email\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$1</a>");
        bbMap.put("\\[email=(.+?)\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$2</a>");
        bbMap.put("\\[url\\](.+?)\\[/url\\]", "<a href='$1'>$1</a>");
        bbMap.put("\\[url=(.+?)\\](.+?)\\[/url\\]", "<a href='$1'>$2</a>");
        bbMap.put("\\[youtube\\](.+?)\\[/youtube\\]", "<object width='640' height='380'><param name='movie' value='http://www.youtube.com/v/$1'></param><embed src='http://www.youtube.com/v/$1' type='application/x-shockwave-flash' width='640' height='380'></embed></object>");
        bbMap.put("\\[video\\](.+?)\\[/video\\]", "<video src='$1' />");

        for (Map.Entry entry: bbMap.entrySet()) {
            html = html.replaceAll(entry.getKey().toString(), entry.getValue().toString());
        }

        return html;
    }

    public final void vkontaktePublish(String link, String linkName, String imageURL) {

        VKAccessToken token = VKAccessToken.tokenFromSharedPreferences(getActivity(), VKAccessToken.ACCESS_TOKEN);
            VKAccessToken curToken = VKAccessToken.currentToken();
        if ((curToken == null) || curToken.isExpired()) {
            VKSdk.login(getActivity(), Constants.SCOPE);
       //     Toast.makeText(getActivity(), "Требуется авторизация. После нее повторите попытку публикации", Toast.LENGTH_LONG).show();
        } else {
            //  Uri imageUri = Uri.parse(imageURL);
            //  Bitmap b = null;
            //   try {
            //       b = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            //   } catch (IOException e) {
            //       e.printStackTrace();
            //   }

          //  final Bitmap b = ImageLoader.getInstance().loadImageSync(imageURL);
            // VKPhotoArray photos = new VKPhotoArray();
            // photos.add(new VKApiPhoto("photo-47200925_314622346"));
            new VKShareDialogBuilder()
                  ////  .setText(message)
                  //          //   .setUploadedPhotos(photos)
                  //  .setAttachmentImages(new VKUploadImage[]{new VKUploadImage(b, VKImageParameters.pngImage())})
                    .setAttachmentLink(linkName, link)
                    .setText(linkName)
                            //.setText(link)
                    .setShareDialogListener(new VKShareDialog.VKShareDialogListener() {

                            @Override
                            public void onVkShareComplete(int postId) {
                                    Toast.makeText(getActivity(), "Запись успешно опубликована", Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onVkShareCancel() {
                                    Toast.makeText(getActivity(), "Публикация отменена", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onVkShareError(VKError error) {
                                    Toast.makeText(getActivity(), "Произошла ошибка при публикации записи", Toast.LENGTH_LONG).show();
                            }
                    }).show(getChildFragmentManager(), "VK_SHARE_DIALOG");
        }
    }
  //  }

    private static void recycleBitmap(@Nullable final Bitmap bitmap) {
        if (bitmap != null) {
            bitmap.recycle();
        }
    }


    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "******onServiceConnected*****");
            MusicBinder binder = (MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass catalogue
            musicBound = true;
            String id = musicSrv.getIdNews();
            if(id != null)
            {
                if (UserLab.getInstance().isLikedNews(id)) {
                    Log.d(TAG, "@@@@@@ liked");
                    btnLike.setImageResource(R.drawable.ic_is_liked);
                } else {
                    Log.d(TAG, "@@@@@@ removed");
                    btnLike.setImageResource(R.drawable.ic_like);
                }
            }
            volumeSeekBar.setProgress(musicSrv.getVolume());
                playSong(getArguments().getString(MediaPlayerFragmentActivity.ARG_AUDIO_ID));

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };
    public  void connectService()
    {
        if(playIntent==null){
            playIntent = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }
    }
    @Override
    public void onStop() {
        Log.d(TAG, "******onStop*****");
        super.onStop();
    //    getActivity().unbindService(musicConnection);
    }
    //start and bind the service when the activity starts
    @Override
    public  void onStart() {
        super.onStart();
        Log.d(TAG, "******onStart*****");
       connectService();

    }

    //user song select
    /*public void songPicked(View view){
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();


    }*/

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                musicSrv.setShuffle();
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicSrv=null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    //method to retrieve song info from device
    public void getSongList(){
        //query external audio
  /*      mNewsList = NewsLab.getInstance().getNews();*/
      /*  ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        //iterate over results if valid
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //ic_add songs to catalogue
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });*/
    }

    public void  playSong(@Nullable String newsId){
        // Play song
        String currentId = "";
     //   Log.d(TAG,"@@@@@@@ url "+ audioUrl);
        if(newsId !=null)
        {
            try
            {
                  //  currentId = PlayList.getInstance().getNewsList().get(musicSrv.getSongPosition()).getId();

                    News n = PlayList.getInstance().getCurrent();
                    if (n != null) {
                            currentId = n.getId();
                    }
            } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
            }


         int location =    PlayList.getInstance().getPosition(newsId);
            if(!(currentId.equals(newsId) /*&& location == musicSrv.getSongPosition()*/))
            {
                Log.d(TAG,"@@@@@@@ playsong "+ newsId);
                musicSrv.setSong(location);
                if(musicSrv.playSong())
                {
                    btnPlay.setImageResource(R.drawable.ic_media_pause);
                    songProgressBar.setProgress(0);
                    songProgressBar.setMax(100);
                }
                // Displaying Song title
                //      String songTitle = songList.get(songIndex).getTitle();
                //        songTitleLabel.setText(songTitle);
            }



        }


            // Updating progress bar
        //    updateMediaPlayer();


    }
    public void updateMediaPlayer() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable stream
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
        //    Log.d(TAG, "update mediaTask");
            if(musicSrv != null) {
                if (!musicSrv.isNullPlayer()) {
                    if (!musicSrv.isPaused()) {
                        long totalDuration = musicSrv.getDur();
                        long currentDuration = musicSrv.getPosn();
                        if (newSongValue != musicSrv.getNewSongValue()) {
                            String id = musicSrv.getIdNews();
                            if(id != null)
                            {
                                if (UserLab.getInstance().isLikedNews(id)) {
                                    Log.d(TAG, "@@@@@@ liked");
                                    btnLike.setImageResource(R.drawable.ic_is_liked);
                                } else {
                                    Log.d(TAG, "@@@@@@ removed");
                                    btnLike.setImageResource(R.drawable.ic_like);
                                }
                            }
                            songTitleLabel.setText(musicSrv.getSongTitle());
                                songContent.setWebViewClient(new WebViewClient() {
                                    @Override
                                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        if (url != null && url.contains("www.youtube"))
                                        {
                                            Log.d(TAG, "@@@@@@@@ url: " +url );
                                        }
                                                if (url != null && (url.startsWith("http://")||url.startsWith("https://"))) {
                                                        view.getContext().startActivity(
                                                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                                        return true;
                                                } else {
                                                        return false;
                                                }
                                        }
                                });
                                songContent.setWebChromeClient(new WebChromeClient());
                                songContent.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                                songContent.getSettings().setJavaScriptEnabled(true);
                                songContent.getSettings().setPluginState(WebSettings.PluginState.ON);
                                songContent.getSettings().setDefaultFontSize(16);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                songContent.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                            } else {
                                songContent.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
                            }
                            songContent.getSettings().setDefaultTextEncodingName("utf-8");
                                songContent.setBackgroundColor(Color.TRANSPARENT);
                               // songContent.getSettings().setLoadWithOverviewMode(true);
                                //songContent.getSettings().setUseWideViewPort(true);
                            String content = musicSrv.getSongContent();
                          /*  String base64 = null;
                            try {
                                base64 = Base64.encodeToString(unescaped.getBytes("UTF-8"), Base64.DEFAULT);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                base64= unescaped;
                            }
                            */
                            content = correctHtml(content);
                            songContent.loadData(getHtmlData(content), "text/html; charset=utf-8",null);
                           // songContent.setText(Html.fromHtml(bbcode(TEST_HTML)));
                         //   songContent.setText(musicSrv.getSongContent());
                            String newsPicture = musicSrv.getNewsPictureUrl();
                            newsPicture = InternetHelper.toCorrectLink(newsPicture);
                            imageLoader.displayImage(newsPicture, songImageView, DisplayImageLoaderOptions.getInstance());
                            newsDate.setText(utils.getDateTimeFormat(musicSrv.getNewsDate()));
                            newSongValue = musicSrv.getNewSongValue();
                        }
                        if (playingValue != musicSrv.isPlaying()) {
                            if (playingValue) {
                                btnPlay.setImageResource(R.drawable.ic_media_play);
                            } else {
                                btnPlay.setImageResource(R.drawable.ic_media_pause);

                            }
                            playingValue = !playingValue;
                        }

                        songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
                        songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

                        // Updating progress bar
                        int progress = (utils.getProgressPercentage(currentDuration, totalDuration));
                        //Log.d("Progress", ""+progress);
                   //     Log.d(TAG, "Progress " + progress + " bufferedProgress "+musicSrv.getBufferPosition());
                        songProgressBar.setProgress(progress);
                        songProgressBar.setSecondaryProgress(musicSrv.getBufferPosition());

                    }
                }
            }
            // Running this stream after 100 milliseconds
               mHandler.postDelayed(this, 100);
        }
    };
    private String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{max-width: 100%;max-height: 100%;object-fit: contain;}</style></head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }
    String correctHtml(String html)
    {
        StringBuffer replacedBuf = new StringBuffer(html.length());

        for(char c : html.toCharArray()) {
            switch(c) {
                case '#' :
                    replacedBuf.append("%23");
                    break;
                case '%':
                    replacedBuf.append("%25");
                    break;
                case '\'':
                    replacedBuf.append("%27");
                    break;
                case '?':
                    replacedBuf.append("%3f");
                    break;
                default:
                    replacedBuf.append(c);
            }
        }
        String escaped =replacedBuf.toString();
        String unescaped = StringEscapeUtils.unescapeHtml4(escaped);
        return unescaped;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = musicSrv.getDur();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        musicSrv.seek(currentPosition);

        // updateFragment timer progress again
     updateMediaPlayer();
    }



    @Override
    public void onPause(){
        super.onPause();
        mHandler.removeCallbacks(mUpdateTimeTask);
        Log.d(TAG, "******onPause*****");
    }

    public static MediaPlayerFragment newInstance(String audioId) {
        MediaPlayerFragment fragment = new MediaPlayerFragment();
        Bundle args = new Bundle();
        args.putString(MediaPlayerFragmentActivity.ARG_AUDIO_ID, audioId);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(musicBound)
        {
            getActivity().unbindService(musicConnection);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
