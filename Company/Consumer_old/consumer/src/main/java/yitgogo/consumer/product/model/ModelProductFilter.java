package yitgogo.consumer.product.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ModelProductFilter {
	/**
	 * {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],
	 * "totalCount"
	 * :1,"dataMap":{"brandList":[{"id":149,"brandName":"长虹","brevityCode"
	 * :"ch","classValueSet"
	 * :null},{"id":150,"brandName":"美的","brevityCode":"md",
	 * "classValueSet":null}
	 * ,{"id":167,"brandName":"TCL","brevityCode":"TCL","classValueSet"
	 * :null},{"id"
	 * :168,"brandName":"奥克斯","brevityCode":"aks","classValueSet":null
	 * },{"id":189
	 * ,"brandName":"格力","brevityCode":"gl","classValueSet":null},{"id"
	 * :191,"brandName"
	 * :"海尔","brevityCode":"he","classValueSet":null},{"id":193,"brandName"
	 * :"海信",
	 * "brevityCode":"hx","classValueSet":null},{"id":235,"brandName":"松下",
	 * "brevityCode"
	 * :"sx","classValueSet":null},{"id":245,"brandName":"小天鹅","brevityCode"
	 * :"xte"
	 * ,"classValueSet":null},{"id":261,"brandName":"格兰仕","brevityCode":"gls"
	 * ,"classValueSet"
	 * :null},{"id":286,"brandName":"三菱重工","brevityCode":"slzg","classValueSet"
	 * :null
	 * },{"id":337,"brandName":"先科","brevityCode":"xk","classValueSet":null}
	 * ,{"id"
	 * :377,"brandName":"月兔","brevityCode":"yt","classValueSet":null},{"id"
	 * :378,"brandName"
	 * :"澳柯玛","brevityCode":"akm","classValueSet":null},{"id":388
	 * ,"brandName":"新科"
	 * ,"brevityCode":"xk","classValueSet":null},{"id":392,"brandName"
	 * :"三菱日机","brevityCode"
	 * :"slrj","classValueSet":null},{"id":413,"brandName":"志高"
	 * ,"brevityCode":"zg"
	 * ,"classValueSet":null},{"id":423,"brandName":"春兰","brevityCode"
	 * :"cl","classValueSet"
	 * :null},{"id":483,"brandName":"大金","brevityCode":"dj",
	 * "classValueSet":null}
	 * ,{"id":610,"brandName":"统帅","brevityCode":"ts","classValueSet"
	 * :null},{"id"
	 * :850,"brandName":"POS-宝士","brevityCode":"POSbs","classValueSet"
	 * :null},{"id"
	 * :1047,"brandName":"科龙","brevityCode":"kl","classValueSet":null
	 * },{"id":1078
	 * ,"brandName":"东方三菱 ","brevityCode":"dfsl","classValueSet":null
	 * },{"id":1118
	 * ,"brandName":"欧美","brevityCode":"om","classValueSet":null},{"id"
	 * :1120,"brandName"
	 * :"苏美","brevityCode":"sm","classValueSet":null},{"id":1157
	 * ,"brandName":"美博"
	 * ,"brevityCode":"mb","classValueSet":null},{"id":1217,"brandName"
	 * :"新迎燕","brevityCode"
	 * :"xyy","classValueSet":null}],"attributeExtendList":[{
	 * "id":20,"attributeExtendName"
	 * :"类型","attributeValueExtendSet":[{"id":689,"attExtendName"
	 * :"强劲制暖","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":806,
	 * "attExtendName":"变频壁挂式空调","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":709,"attExtendName":"全净化功能","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":713,"attExtendName":"健康变频",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":679,"attExtendName"
	 * :"双超舒适","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":81,"attExtendName"
	 * :"柜式","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":671,"attExtendName"
	 * :"面板遥控器","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":190
	 * ,"attExtendName":"一晚一度电","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":693,"attExtendName":"单冷型","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":702,"attExtendName":"一键双模",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":690,"attExtendName"
	 * :"冷暖定频","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":682,
	 * "attExtendName":"人体状态感知","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":220,"attExtendName":"静星变频空调","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":792,"attExtendName":"除PM2.5冷暖变频",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":790,"attExtendName"
	 * :"除甲醛","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":724,"attExtendName"
	 * :"单冷定速","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":708,
	 * "attExtendName":"人性化睡眠模式","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":728,"attExtendName":"无氟变频","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":669,"attExtendName":"除霾霸主",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":701,"attExtendName"
	 * :"双模变频","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":735,
	 * "attExtendName":"全自动智控","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":804,"attExtendName":"冷暖无氟直流变频","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":801,"attExtendName":"冷暖直流变频",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":721,"attExtendName"
	 * :"双排蒸发器","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":725
	 * ,"attExtendName":"强力除湿","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":794,"attExtendName":"极速冷暖变频","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":678,"attExtendName":"物联控制",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":681,"attExtendName"
	 * :"送风超远","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":687,
	 * "attExtendName":"长虹物联网","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":373,"attExtendName":"冷暖电辅","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":673,"attExtendName":"单冷移动",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":694,"attExtendName"
	 * :"无需排水","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":727,
	 * "attExtendName":"干燥防霉","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":39,"attExtendName":"定频","attriuteExtendBeanMap"
	 * :{"id":20,"name": "类型"},"sort":0},{"id":719,"attExtendName":"自动水洗",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":707,"attExtendName":"治温提效系统",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":736,"attExtendName"
	 * :"超远超强送风","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":805
	 * ,"attExtendName":"直流家用冷暖空调","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},
	 * "sort":0},{"id":35,"attExtendName":"立柜","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":711,"attExtendName":"净化系统",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":675,"attExtendName"
	 * :"精确变频","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":714,
	 * "attExtendName":"粉尘检测技术","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":667,"attExtendName":"双层过滤","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":705,"attExtendName":"静音节能",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":672,"attExtendName"
	 * :"风管机","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":30,"attExtendName"
	 * :"挂壁式","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":414,"attExtendName"
	 * :"挂式","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":715,"attExtendName"
	 * :"直流变频","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":807,
	 * "attExtendName":"冷暖全直流变频","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":733,"attExtendName":"冷暖不怕","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":686,"attExtendName":"军工品质",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":422,"attExtendName"
	 * :"挂式n","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":722,"attExtendName"
	 * :"名牌压缩机","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":723
	 * ,"attExtendName":"冷暖定速","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":810,"attExtendName":"迅猛单冷定速","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":691,"attExtendName":"钛金移动空调",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":726,"attExtendName"
	 * :"快速制冷","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":716,
	 * "attExtendName":"冷媒技术","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":802,"attExtendName":"变频家用冷暖","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":685,"attExtendName":"物联网空调",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":488,"attExtendName"
	 * :"挂机","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":212,"attExtendName"
	 * :"悦弧全直流变频空","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":
	 * 94,"attExtendName":"柜机","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":31,"attExtendName":"立柜式","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":680,"attExtendName":"运行超静",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":703,"attExtendName"
	 * :"智在节能","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":710,
	 * "attExtendName":"双模变频技术","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":696,"attExtendName":"钛金两器制冷","attriuteExtendBeanMap"
	 * :{"id":20,"name"
	 * :"类型"},"sort":0},{"id":291,"attExtendName":"空调","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":720,"attExtendName":"高效除甲醛",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":224,"attExtendName"
	 * :"变频空调","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":668,
	 * "attExtendName":"高效制暖","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":799,"attExtendName":"冷暖无氟","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":700,"attExtendName":"艺术化柜机",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":791,"attExtendName"
	 * :"除甲醛冷暖节能","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":699
	 * ,"attExtendName":"变频柜机","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":712,"attExtendName":"粉尘检测","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":677,"attExtendName":"柱式柜机",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":695,"attExtendName"
	 * :"单冷定频","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":670,
	 * "attExtendName":"红外智能感应","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":684,"attExtendName":"长虹智能云端","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":674,"attExtendName":"柱形柜机",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":793,"attExtendName"
	 * :"物联网","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":704,"attExtendName"
	 * :"直流变频驱动","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":83
	 * ,"attExtendName":"壁挂式","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":36,"attExtendName":"变频","attriuteExtendBeanMap"
	 * :{"id":20,"name": "类型"},"sort":0},{"id":225,"attExtendName":"定频空调",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":666,"attExtendName":"冷暖变频",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":734,"attExtendName"
	 * :"定频挂机","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":803,
	 * "attExtendName":"定速冷暖","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":683,"attExtendName":"人体温度场","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":737,"attExtendName":"变频冷暖",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":808,"attExtendName"
	 * :"变频壁挂式","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":698
	 * ,"attExtendName":"挂式冷暖定速","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":692,"attExtendName":"钛金两器","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":585,"attExtendName":"立式柜机",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":809,"attExtendName"
	 * :"变频壁挂","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":795,
	 * "attExtendName":"迅猛冷暖定速","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":798,"attExtendName":"家用单冷定速","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":706,"attExtendName":"HDSC净显系统",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":676,"attExtendName"
	 * :"低噪音","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":697,"attExtendName"
	 * :"挂式冷暖变频","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":82
	 * ,"attExtendName":"壁挂机","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0},{"id":797,"attExtendName":"无氟直流变频","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":800,"attExtendName":"挂式变频",
	 * "attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":372,"attExtendName"
	 * :"冷暖空调","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort":0},{"id":688,
	 * "attExtendName":"简洁外观","attriuteExtendBeanMap"
	 * :{"id":20,"name":"类型"},"sort"
	 * :0}],"pcvSe":null,"sort":0},{"id":49,"attributeExtendName"
	 * :"功能","attributeValueExtendSet"
	 * :[{"id":447,"attExtendName":"i酷变频柜机3","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":427,"attExtendName":"冷静王变频",
	 * "attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":446,"attExtendName"
	 * :"T迪变频柜机3","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":184
	 * ,"attExtendName":"T迪","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort"
	 * :0},{"id":582,"attExtendName":"冷俊星PA402直流（APF新能效三级）"
	 * ,"attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":436,"attExtendName"
	 * :"鸿运满堂变频柜机3","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id"
	 * :581,"attExtendName":"变频.冷暖型","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"
	 * },"sort":0},{"id":730,"attExtendName":"定频柜机","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":423,"attExtendName":"冷静王2",
	 * "attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":729,"attExtendName"
	 * :"变频柜机","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":578,
	 * "attExtendName":"尚弧KB全直流（APF新能一级）"
	 * ,"attriuteExtendBeanMap":{"id":49,"name"
	 * :"功能"},"sort":0},{"id":573,"attExtendName"
	 * :"变频.静音","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":435
	 * ,"attExtendName":"T迪三级","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort"
	 * :0},{"id":409,"attExtendName":"超静音，超长质保，强力除湿"
	 * ,"attriuteExtendBeanMap":{"id"
	 * :49,"name":"功能"},"sort":0},{"id":445,"attExtendName"
	 * :"T迪柜机3","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":93,
	 * "attExtendName":"冷暖","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":
	 * 0},{"id":180,"attExtendName":"变频冷暖空调","attriuteExtendBeanMap"
	 * :{"id":49,"name"
	 * :"功能"},"sort":0},{"id":113,"attExtendName":"定频","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":428,"attExtendName":"冷静王变频3",
	 * "attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":450,"attExtendName"
	 * :"Q畅3","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":182,"attExtendName"
	 * :"变频空调","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":430,
	 * "attExtendName":"冷静宝变频3","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort"
	 * :0},{"id":183,"attExtendName":"定频冷暖","attriuteExtendBeanMap"
	 * :{"id":49,"name" :"功能"},"sort":0},{"id":90,"attExtendName":"氧吧功能",
	 * "attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":451,"attExtendName":"绿满圆3挂机",
	 * "attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":191,"attExtendName"
	 * :"冷暖变频","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":407,
	 * "attExtendName":"超静音，超长质保，强力除湿，电辅加热"
	 * ,"attriuteExtendBeanMap":{"id":49,"name"
	 * :"功能"},"sort":0},{"id":416,"attExtendName"
	 * :"Q迪变频3","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":385
	 * ,"attExtendName":"超静音，超长质保，强力除湿，电辅加热 变频"
	 * ,"attriuteExtendBeanMap":{"id":49,
	 * "name":"功能"},"sort":0},{"id":584,"attExtendName"
	 * :"直流.变频","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":426
	 * ,"attExtendName":"冷静王变频2","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort"
	 * :0},{"id":432,"attExtendName":"U雅变频3","attriuteExtendBeanMap"
	 * :{"id":49,"name"
	 * :"功能"},"sort":0},{"id":383,"attExtendName":"超静音，超长质保，强力除湿，电辅加 变频"
	 * ,"attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":112,"attExtendName"
	 * :"变频","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":415,"attExtendName"
	 * :"Q力（三级）","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":406
	 * ,"attExtendName":"超长质保，强力除湿，电辅加热"
	 * ,"attriuteExtendBeanMap":{"id":49,"name":
	 * "功能"},"sort":0},{"id":431,"attExtendName"
	 * :"U酷变频3","attriuteExtendBeanMap":
	 * {"id":49,"name":"功能"},"sort":0},{"id":388
	 * ,"attExtendName":"圆柱型柜机，超静音，超长质保，电辅加热 变频"
	 * ,"attriuteExtendBeanMap":{"id":49
	 * ,"name":"功能"},"sort":0},{"id":434,"attExtendName"
	 * :"T迪变频1","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":214
	 * ,"attExtendName":"直流变频","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort"
	 * :0},{"id":390,"attExtendName":"圆柱型柜机，超静音，超长质保，强力除湿"
	 * ,"attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":181,"attExtendName"
	 * :"超低频变频空调","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort":0},{"id":452
	 * ,"attExtendName":"绿满园3挂机","attriuteExtendBeanMap"
	 * :{"id":49,"name":"功能"},"sort"
	 * :0}],"pcvSe":null,"sort":0},{"id":69,"attributeExtendName"
	 * :"颜色","attributeValueExtendSet"
	 * :[{"id":587,"attExtendName":"霓粉金","attriuteExtendBeanMap"
	 * :{"id":69,"name":
	 * "颜色"},"sort":0},{"id":480,"attExtendName":"红色","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":731,"attExtendName":"变频",
	 * "attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":586,"attExtendName"
	 * :"钛金","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":375,"attExtendName"
	 * :"香槟色","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":389,"attExtendName"
	 * :"银色","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":784,"attExtendName"
	 * :"咖啡金","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":574,"attExtendName"
	 * :"陶瓷白","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":783,"attExtendName"
	 * :"珍珠白","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":386,"attExtendName"
	 * :"月光白","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":449,"attExtendName"
	 * :"深银","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":374,"attExtendName"
	 * :"酒红色","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":384,"attExtendName"
	 * :"白色","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":433,"attExtendName"
	 * :"银灰","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":579,"attExtendName"
	 * :"水密红","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":732,"attExtendName"
	 * :"定频","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":448,"attExtendName"
	 * :"深色","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":588,"attExtendName"
	 * :"紫色","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0},{"id":408,"attExtendName"
	 * :"印花","attriuteExtendBeanMap"
	 * :{"id":69,"name":"颜色"},"sort":0}],"pcvSe":null
	 * ,"sort":0},{"id":75,"attributeExtendName"
	 * :"格力空调","attributeValueExtendSet"
	 * :[{"id":505,"attExtendName":"1匹","attriuteExtendBeanMap"
	 * :{"id":75,"name":"格力空调"
	 * },"sort":0},{"id":500,"attExtendName":"大1.5匹","attriuteExtendBeanMap"
	 * :{"id":75,"name":"格力空调"},"sort":0},{"id":490,"attExtendName":"大1匹",
	 * "attriuteExtendBeanMap"
	 * :{"id":75,"name":"格力空调"},"sort":0},{"id":481,"attExtendName"
	 * :"2匹","attriuteExtendBeanMap"
	 * :{"id":75,"name":"格力空调"},"sort":0},{"id":486,
	 * "attExtendName":"3匹","attriuteExtendBeanMap"
	 * :{"id":75,"name":"格力空调"},"sort"
	 * :0},{"id":489,"attExtendName":"1.5匹","attriuteExtendBeanMap"
	 * :{"id":75,"name":"格力空调"},"sort":0}],"pcvSe":null,"sort":0},{"id":93,
	 * "attributeExtendName"
	 * :"冷暖","attributeValueExtendSet":[{"id":575,"attExtendName"
	 * :"冷暖双用","attriuteExtendBeanMap"
	 * :{"id":93,"name":"冷暖"},"sort":0},{"id":665,
	 * "attExtendName":"变频冷暖","attriuteExtendBeanMap"
	 * :{"id":93,"name":"冷暖"},"sort"
	 * :0},{"id":583,"attExtendName":"冷暖型","attriuteExtendBeanMap"
	 * :{"id":93,"name":"冷暖"},"sort":0},{"id":504,"attExtendName":"冷 2800",
	 * "attriuteExtendBeanMap"
	 * :{"id":93,"name":"冷暖"},"sort":0},{"id":577,"attExtendName"
	 * :"冷暖","attriuteExtendBeanMap"
	 * :{"id":93,"name":"冷暖"},"sort":0},{"id":506,"attExtendName"
	 * :"冷2800","attriuteExtendBeanMap"
	 * :{"id":93,"name":"冷暖"},"sort":0}],"pcvSe":
	 * null,"sort":0}],"attributeList":
	 * [{"id":492,"attributeName":"功率","attributeValueSet"
	 * :[{"id":421,"attName":"大1匹"
	 * ,"attriuteBeanMap":{"id":492,"name":"功率"},"sort"
	 * :0},{"id":424,"attName":"3匹"
	 * ,"attriuteBeanMap":{"id":492,"name":"功率"},"sort"
	 * :0},{"id":425,"attName":"小1.5匹"
	 * ,"attriuteBeanMap":{"id":492,"name":"功率"},"sort"
	 * :0},{"id":488,"attName":"大1.5匹"
	 * ,"attriuteBeanMap":{"id":492,"name":"功率"},"sort"
	 * :0},{"id":489,"attName":"1匹"
	 * ,"attriuteBeanMap":{"id":492,"name":"功率"},"sort"
	 * :0},{"id":490,"attName":"小1匹"
	 * ,"attriuteBeanMap":{"id":492,"name":"功率"},"sort"
	 * :0},{"id":491,"attName":"2匹"
	 * ,"attriuteBeanMap":{"id":492,"name":"功率"},"sort"
	 * :0},{"id":518,"attName":"1000W以下"
	 * ,"attriuteBeanMap":{"id":492,"name":"功率"}
	 * ,"sort":0},{"id":519,"attName":"1001W-2000W"
	 * ,"attriuteBeanMap":{"id":492,"name"
	 * :"功率"},"sort":0},{"id":520,"attName":"2800W"
	 * ,"attriuteBeanMap":{"id":492,"name"
	 * :"功率"},"sort":0},{"id":521,"attName":"3001W以上"
	 * ,"attriuteBeanMap":{"id":492
	 * ,"name":"功率"},"sort":0},{"id":539,"attName":"200W及以下"
	 * ,"attriuteBeanMap":{"id"
	 * :492,"name":"功率"},"sort":0},{"id":540,"attName":"201-500W"
	 * ,"attriuteBeanMap"
	 * :{"id":492,"name":"功率"},"sort":0},{"id":541,"attName":"501-800W"
	 * ,"attriuteBeanMap"
	 * :{"id":492,"name":"功率"},"sort":0},{"id":542,"attName":"800W及以上"
	 * ,"attriuteBeanMap"
	 * :{"id":492,"name":"功率"},"sort":0},{"id":962,"attName":"大3匹"
	 * ,"attriuteBeanMap"
	 * :{"id":492,"name":"功率"},"sort":0},{"id":1506,"attName":"1.5匹"
	 * ,"attriuteBeanMap"
	 * :{"id":492,"name":"功率"},"sort":0}],"pcvSe":null,"sort":0
	 * }]},"object":null}
	 */

	List<ModelAttrType> brands = new ArrayList<ModelAttrType>();
	List<ModelAttrType> attrTypes = new ArrayList<ModelAttrType>();
	List<ModelAttrType> attrTypeExtends = new ArrayList<ModelAttrType>();

	public ModelProductFilter() {

	}

	public ModelProductFilter(JSONObject object) throws JSONException {
		JSONArray brandArray = object.optJSONArray("brandList");
		if (brandArray != null) {
			if (brandArray.length() > 0) {
				brands.add(new ModelAttrType(null, brandArray,
						ModelAttrType.TYPE_BRAND));
			}
		}
		JSONArray attributeExtendArray = object
				.optJSONArray("attributeExtendList");
		if (attributeExtendArray != null) {
			if (attributeExtendArray.length() > 0) {
				for (int i = 0; i < attributeExtendArray.length(); i++) {
					attrTypeExtends.add(new ModelAttrType(attributeExtendArray
							.getJSONObject(i), null,
							ModelAttrType.TYPE_ATTR_EXTEND));
				}
			}
		}
		JSONArray attributeArray = object.optJSONArray("attributeList");
		if (attributeArray != null) {
			if (attributeArray.length() > 0) {
				for (int i = 0; i < attributeArray.length(); i++) {
					attrTypes.add(new ModelAttrType(attributeArray
							.getJSONObject(i), null, ModelAttrType.TYPE_ATTR));
				}
			}
		}
	}

	public List<ModelAttrType> getAllAttrs() {
		List<ModelAttrType> allAttrs = new ArrayList<ModelAttrType>();
		allAttrs.addAll(brands);
		allAttrs.addAll(attrTypes);
		allAttrs.addAll(attrTypeExtends);
		return allAttrs;
	}

	public List<ModelAttrType> getBrands() {
		return brands;
	}

	public List<ModelAttrType> getAttrTypes() {
		return attrTypes;
	}

	public List<ModelAttrType> getAttrTypeExtends() {
		return attrTypeExtends;
	}
}
