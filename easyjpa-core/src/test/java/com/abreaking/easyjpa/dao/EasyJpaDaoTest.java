package com.abreaking.easyjpa.dao;

import com.abreaking.easyjpa.dao.condition.Condition;
import com.abreaking.easyjpa.dao.condition.Page;
import com.abreaking.easyjpa.dao.impl.EasyJpaDaoImpl;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * test for easyDao
 * @author liwei_paas
 * @date 2020/12/30
 */
public class EasyJpaDaoTest  {

    EasyJpaDao dao = new EasyJpaDaoImpl(MyDaoTest.sqlExecutor);

    private void prettyPrint(List list){

        if (list.isEmpty()){
            System.out.println("result is empty");
        }
        System.out.println("result size is :"+list.size());
        for (Object o : list){
            System.out.println(o);
        }
    }

    @Test
    public void query1() {
        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setSceneCreateUser("沈哲");
        List<SceneInfo> list = dao.query(sceneInfo);
        prettyPrint(list);
    }

    @Test
    public void queryEasyJpa() {
        EasyJpa easyJpa = new EasyJpa(SceneInfo.class);
        String name = "销";
        easyJpa.or(Condition.like("sceneNameCn",name));
        easyJpa.or(Condition.like("scene_name_en",name));
        easyJpa.or(Condition.like("scene_desc",name));
        easyJpa.and(Condition.to("indexId",">",10));
        easyJpa.orderBy("sceneNameCn",false);
        easyJpa.limit(2,3);
        List list = dao.query(easyJpa);
        prettyPrint(list);
    }

    @Test
    public void queryprepareSqlvalues() {
        String prepareSql = "SELECT * FROM bcoc_scene_info WHERE index_id > ?   AND (scene_name_cn LIKE ? OR scene_name_en LIKE ? OR scene_desc LIKE ?  ) ORDER BY scene_name_cn DESC LIMIT ?,? ";
        List<Map<String, Object>> list = dao.query(prepareSql,10,"%销%", "%销%", "%销%", 2, 3);
        prettyPrint(list);


    }

    @Test
    public void query() {
        String prepareSql = "SELECT * FROM bcoc_scene_info WHERE index_id > ?   AND (scene_name_cn LIKE ? OR scene_name_en LIKE ? OR scene_desc LIKE ?  ) ORDER BY scene_name_cn DESC LIMIT ?,? ";
        List<SceneInfo> list = dao.query(prepareSql, new Object[]{10,"%销%", "%销%", "%销%", 2, 3}, SceneInfo.class);
        prettyPrint(list);
    }

    @Test
    public void queryplaceholderSqlvaluesMap() {
        String placeholderSql = "SELECT * FROM ${SceneInfo} WHERE 1=1 AND index_id > #{indexId}  AND " +
                "(scene_name_cn LIKE #{name} OR scene_name_en LIKE #{name} OR scene_desc LIKE #{name}  ) ORDER BY ${orderBy} LIMIT #{pageStart},#{pageSize} ";
        Map<String, Object> valuesMap = new HashMap<>();
        valuesMap.put("indexId",10);
        valuesMap.put("name","%销%");
        valuesMap.put("pageStart",2);
        valuesMap.put("pageSize",4);
        valuesMap.put("orderBy","scene_name_cn ASC");
        List<SceneInfo> list = dao.query(placeholderSql, valuesMap, SceneInfo.class);
        prettyPrint(list);
    }

    @Test
    public void queryplaceholderSqlEntity() {
        String placeholderSql = "select * from ${tableName} where index_id<#{indexId} and scene_version=#{sceneVersion}";
        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setIndexId(10L);
        sceneInfo.setSceneVersion("0.1");
        prettyPrint(dao.query(placeholderSql,sceneInfo,SceneInfo.class));
    }


    public <T> List<T> query(String placeholderSql, Object entity, Class<T> returnType) {
        return null;
    }


    public <T> Page<T> queryByPage(EasyJpa<T> condition, Page page) {
        return null;
    }

    @Test
    public void get() {
        System.out.println(dao.get(SceneInfo.class,21));
    }

    @Test
    public void updateEntity() {
        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setIndexId(2L);
        sceneInfo.setSceneVersion("0.3");
        sceneInfo.setSceneId("1008611");
        dao.update(sceneInfo);
        System.out.println(dao.get(SceneInfo.class,2L));
    }

    @Test
    public void updateEntityByCondition() {
        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setSceneVersion("1.2");

        EasyJpa easyJpa = new EasyJpa(SceneInfo.class);
        easyJpa.and(Condition.equal("sceneVersion","0.3"));
        easyJpa.and(Condition.between("indexId",3,10));
        dao.update(sceneInfo,easyJpa);
    }

    @Test
    public void delete() {
        //dao.delete(SceneInfo.class,2);
        EasyJpa easyJpa = new EasyJpa(SceneInfo.class);
        easyJpa.and(Condition.like("sceneId","B2012%"));
        easyJpa.and(Condition.to("indexId","=",7));
        dao.delete(easyJpa);
    }

    @Test
    public <T> void insert() {
        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setSceneId("12345");
        sceneInfo.setSceneVersion("1.99");
        sceneInfo.setSceneNameCn("随便");
        dao.insert(sceneInfo);
    }


    public void execute(String prepareSql, Object... values) {
        prepareSql = "CREATE TABLE `bcoc_scene_info` (\n" +
                "  `index_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',\n" +
                "  `scene_id` varchar(20) NOT NULL COMMENT '场景编码',\n" +
                "  `scene_version` varchar(10) NOT NULL,\n" +
                "  `scene_name_cn` varchar(50) DEFAULT NULL COMMENT '场景中文名称',\n" +
                "  `scene_name_en` varchar(50) DEFAULT NULL COMMENT '场景英文名称',\n" +
                "  `scene_desc` varchar(1000) DEFAULT NULL COMMENT '场景描述',\n" +
                "  `scene_menu_id` bigint(20) DEFAULT NULL COMMENT '场景归属目录ID',\n" +
                "  `scene_star_level` int(11) DEFAULT NULL COMMENT '场景星级',\n" +
                "  `scene_status` int(2) DEFAULT NULL COMMENT '能力状态: 0 注销 1 开发中 2 测试中 3 审核中 4 待上架（审核通过和下架包含在这里） 5 已上架 ',\n" +
                "  `scene_area` varchar(20) DEFAULT NULL COMMENT '场景归属业务域',\n" +
                "  `scene_center` varchar(20) DEFAULT NULL COMMENT '场景归属中心',\n" +
                "  `scene_type` varchar(20) DEFAULT NULL COMMENT '场景业务类型',\n" +
                "  `scene_flowimage_path` varchar(100) DEFAULT NULL COMMENT '流程图片路径',\n" +
                "  `scene_image_path` varchar(100) DEFAULT NULL COMMENT '原型图片路径',\n" +
                "  `scene_url` varchar(100) DEFAULT NULL COMMENT '访问URL',\n" +
                "  `scene_custom_tag` varchar(50) DEFAULT NULL COMMENT '场景自定义标签',\n" +
                "  `require_id` varchar(100) DEFAULT NULL COMMENT '需求编码',\n" +
                "  `scene_create_user` varchar(50) DEFAULT NULL COMMENT '场景创建人',\n" +
                "  `scene_create_time` datetime DEFAULT NULL COMMENT '场景创建时间',\n" +
                "  `scene_create_user_phone` varchar(50) DEFAULT NULL COMMENT '场景创建人联系方式',\n" +
                "  `scene_update_user` varchar(50) DEFAULT NULL COMMENT '场景更新人',\n" +
                "  `scene_update_time` datetime DEFAULT NULL COMMENT '场景更新时间',\n" +
                "  `scene_update_user_phone` varchar(50) DEFAULT NULL COMMENT '场景更新人联系方式',\n" +
                "  PRIMARY KEY (`index_id`,`scene_id`,`scene_version`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8";
        dao.execute(prepareSql);
    }


    @Test
    public void execute2( ) {
        String sql = "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('4','B201219135482656','0.1','卡券营销','sn_004','面向线上、线下渠道，结合萤火魔方、省公司及专业公司营销平台、外部渠道等平台，通过人+券+场的业务模式，连接用户与渠道，助力渠道实现拉新、促活、增收等业务目标。\\r\\n支持用户领取、购买、转赠、兑换；支持商户精准批量发券、触点发券；支持泛渠道商户进行卡券配置、发放、核销。','8','3','5','001','001','001','/images/5.1.1.6.卡券营销.png','','','354','1','沈哲',NULL,'13102343214','沈哲',NULL,'沈哲');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('5','10005','0.1','网格运营','sn_005','网格运营是指整合全部大市场、大网络数据，聚焦“四轮驱动”全业务发展，以综合网格/微网格为最小作战单元，下沉和协同人员、营销、资源、网络等资源，进一步加强对区域市场的执行力、掌控力，积极应对市场竞争，强化网格精细化运营，打好市场发展格局战。','9','5','5','001','001','001','/images/网格运营.png','','','55','1','李海聪',NULL,'13102343214','李海聪',NULL,'李海聪');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('6','B201219135873604','0.1','客户投诉全程可视化','sn_006','客户投诉全程可视化场景，省侧需具备场景支撑能力，展示页面位置包括使用在线公司系统和省侧系统两种情况','3','3','5','001','001','001','/images/5.1.1.2.校园营销.png','','','23','1','代学平',NULL,'13102343214','代学平',NULL,'代学平');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('7','B201219142823254','0.1','政企订单透明化','sn_007','各种业务角色通过业务中台提供的服务能力，利用各类型前端查看跟踪融合订单的执行状态，并根据业务需求调度订单执行。','2','3','5','001','001','001','/images/5.1.2.1.政企订单透明化场景.png','','','125','1','管宁',NULL,'13102343214','管宁',NULL,'管宁');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('8','10008','0.1','AIOps智能运维','sn_008','业务维护，通常是需要维护系统数据、应用程序数据、KPI指标数据，海量数据存在相互的关联影响。且单个指标是否异常，会随着时间、业务高低峰等影响变化。','9','3','5','001','001','001','/images/5.4.1.AIOPS.png','','','67','1','杨玉峰',NULL,'13102343214','杨玉峰',NULL,'杨玉峰');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('9','10009','0.1','携转挽留','sn_009','通过设定营销期望、营销目标，结合携转相关模型，输出不同方案及其目标用户，将识别结果转化为营销力，进行营销策略匹配、渠道时机匹配。例如，携转运营经理通过携转运营应用监控运营指标，针对指标异常等情况作出反应，调用业务中台能力制定营销策略，调用AI中台确定攻守博弈策略和目标客户群，从而支持业务中台细化营销方案，执行营销，最终使得相关指标达到期望。','9','3','5','001','001','001','/images/5.1.1.3.携转挽留.png','','','0','1','杨玉峰',NULL,'13102343214','杨玉峰',NULL,'杨玉峰');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('10','10010','0.1','融合推送','sn_010','针对实时营销、服务提醒、用户促活等场景进行智能的推送通道管控，实现多APP、多短信服务号、多微信公众号、多邮箱的融合推送方式，确保消息100%到达。','9','5','5','001','001','001','/images/融合推送.png','','','0','1','张宗之',NULL,'13102343214','张宗之',NULL,'张宗之');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('11','10011','0.1','智能家居营销','sn_011','智能家居是利用智慧中台的能力，智能分析适合向用户推荐的智能家居套餐和智能设备，结合营销推荐、一键下单等支撑能力，助力智能家居业务的拓展。','9','4','5','001','001','001','/images/5.1.3.2.智能家居营销.png','','','0','1','李海聪',NULL,'13102343214','李海聪',NULL,'李海聪');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('12','10012','0.1','满意度预测','sn_012','通过客户满意度模型，定位与客户满意度强相关的业务指标，并基于这些指标进行客户不满意概率的预测；继而根据预测结果，划分出潜在不满意用户群体，聚焦潜在不满意用户的行为特征并进行分组，按照特征分组支撑相关业务部门进行修复或营销策略的制定，最终结合修复后的效果评估进行闭环管理，实现客户满意的精准修复和提升。','9','3','5','001','001','001','/images/5.2.1.满意度预测（全网统建）.png','','','0','1','李海聪',NULL,'13102343214','李海聪',NULL,'李海聪');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('13','10013','0.1','家庭圈营销','sn_013','家庭圈营销是利用智慧中台的能力，智能识别潜在家庭成员，分析适合向家庭网推荐的套餐，结合营销推荐、一键下单等支撑能力，助力家庭业务的拓展。家庭圈营销分为家庭成员发展和家庭商品推荐两个子场景。','9','5','5','001','001','001','/images/5.1.3.1.家庭圈营销.png','','','0','1','李海聪',NULL,'13102343214','李海聪',NULL,'李海聪');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('14','B201219143650619','0.1','业务预受理+极简下单','sn_014','针对家宽、融合等复杂的业务场景，结合中台对业务受理的整合能力，降低原有前台业务受理的复杂性，通过一键办理模式、及甩单模式的支撑，提升客户的感知与业务受理的效率。 ','6','5','5','001','001','001','/images/业务预受理+极简下单.png','','','0','1','沈哲','2020-12-22 11:35:05','13102343214','沈哲',NULL,'沈哲');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('15','10015','0.1','电子渠道用户拉新促活营销','sn_015','针对线上自有渠道APP、微信公众号、网厅、商城等电子渠道，用户采用互联网化的签到、助力、拼团等活动形式提升用户黏性，运营人员通过萤火·魔方等自助式快营销工具实现活动页面风格、活动形式、营销规则策略、营销资源的快速配置和灵活封装组合，无需开发20分钟内快速上线，最大化利用圈子营销手段进行电子渠道用户拉新、促活，进而引导用户办理。','9','3','5','001','001','001','/images/5.1.1.7.电子渠道用户拉新促活营销.png','','','0','1','李海聪',NULL,'13102343214','李海聪',NULL,'李海聪');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('16','10016','0.1','智能应答','sn_016','构建运维领域知识库或知识图谱，客户端支持语音输入、语音搜索功能，对用户问题进行语音识别、语义理解和知识匹配，识别用户意图，根据知识库检索内容、规则处理后返回相应答案，并可通过语音合成播报来解答用户问题，实现运维领域智能应答服务。','9','4','5','001','001','001','/images/5.2.2.智能应答.png','','','0','1','杨玉峰',NULL,'13102343214','杨玉峰',NULL,'杨玉峰');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('17','10017','0.1','流量套餐营销（实时营销、中高端）','sn_017','支持对营销活动的创建、修改、审批。','9','2','5','001','001','001','/images/5.1.1.1.实时营销.png','','','0','1','刘明航',NULL,'13102343214','刘明航',NULL,'刘明航');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('18','10018','0.1','线上线下协同','sn_018','线上与线下渠道协同，营销触点得到有效延伸，解决不同系统受理壁垒，提高受理效率。对用户，大大缩短了办理时长，丰富了产品的交付方式，提升用户感知。 ','9','3','5','001','001','001','/images/线上线下协同.png','','','0','1','刘明航',NULL,'13102343214','刘明航',NULL,'刘明航');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('19','10019','0.1','业务稽核','sn_019','通过事后的人工线上稽核和自动稽核能力，借助AI自动识别等技术对图片资料进行有效性校验，从电子协议，电子订单，业务欠费，业务客户资料,资金等多业务维度进行业务层面的数据稽核，采用本地稽核，集中稽核等多层级的稽核流程对数据进行稽核。有效规避由于资料问题，数据问题导致的订购风险和资金管控风险,将数据稽核结果推送给各业务平台进行业务层面的处理。 ','9','4','5','001','001','001','/images/5.3.1.业务稽核（全网统建）.png','','','0','1','李海聪',NULL,'13102343214','李海聪',NULL,'李海聪');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('20','10020','0.1','5G终端潜在客户营销','sn_020','针对潜在5G终端客户，在某个营销触点如在5G终端相关的网站页面浏览或搜索5G终端相关关键词时，系统通过插码技术实时抓取客户的访问内容，并与客户当前的消费水平和语音、流量使用水平等相结合计算出5G终端推荐产品。在网厅、商城和手机营业厅上实时向客户推荐相应的5G终端，并引导客户到附近的营业厅进行终端体验。','9','2','5','001','001','001','/images/5.1.1.5.5G终端潜在客户营销.png','','','0','1','高勇洲',NULL,'13102343214','高勇洲',NULL,'高勇洲');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('21','10021','0.1','政企潜在客户挖掘','sn_021','通过爬虫等多种方式收集工商注册、政府各类审批、公示，财务报告等企业活动轨迹等信息，对于采集清洗、挖掘、识别企业的经营范围、公司办公地址、企业员工规模、注册资本、成立年限、所在商圈、所在楼宇、公司财报、年报、找标信息等关联数据。\\r\\n通过AI模型训练集成的潜在客户识别模型和营销产品推荐模型，生成潜在商机任务派发给客户经理。','9','3','5','001','001','001','/images/5.1.1.2.校园营销.png','','','0','1','张永安',NULL,'13102343214','张永安',NULL,'张永安');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('22','10022','0.1','融合业务管理','sn_022','融合业务管理（含服务）主要是指个人业务、家庭业务、集团业务、新业务的融合业务运营、业务支撑和业务办理。典型业务场景如：企业尊享计划、新版飞悦不限量套餐等。','9','3','5','001','001','001','/images/融合业务管理.png','','','0','1','李海聪',NULL,'13102343214','李海聪',NULL,'李海聪');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('23','10023','0.1','校园营销','sn_023','针对校园中的学生及教师等客户，在校园经理组织进行现场营销及主动营销时，通过对客户信息与营销策略的智能匹配生成个性化的营销方案。','9','3','5','001','001','001','/images/5.1.1.2.校园营销.png','','','0','1','杨玉峰',NULL,'13102343214','杨玉峰',NULL,'杨玉峰');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('24','10024','0.1','政企业务商机处理','sn_024','业务中台生成潜在商机后，集团客户经理完成商机任务接单、商机精确挖掘、订单受理、合同签署流程。','9','4','5','001','001','001','/images/5.1.3.3.政企业务商机处理.png','','','0','1','曹睿/胡鹏',NULL,'13102343214','曹睿/胡鹏',NULL,'曹睿/胡鹏');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('25','10025','0.1','四轮及融合市场的运营分析','sn_025','通过构建一体化的集中监控、向导分析模式，从地域、职能等多个角度提供全方位的业务监控、运营分析、异常预警能力，并通过多种数据分析手段实现数据驱动的业务问题发现和经营决策，为日常的市场运营工作提供有力的支持。','9','2','5','001','001','001','/images/四轮及融合市场的运营分析.png','','','0','1','李海聪',NULL,'13102343214','李海聪',NULL,'李海聪');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('26','10026','0.1','任务协同','sn_026','客户经理通过扫街营销单、网格营销、外场营销单、OAO上门营销、常客维系、泛渠道拓展等各线上线下渠道对目标客户进行营销活动的推荐和办理，在各受理系统形成受理信息。','9','3','5','001','001','001','/images/5.1.4.3.任务协同.png','','','0','1','杨玉峰',NULL,'13102343214','杨玉峰',NULL,'杨玉峰');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('27','10027','0.1','5G数据业务智能计费','sn_027','新建5G融合计费系统CCS，构建满足3GPP规范服务化、离在线融合计费架构的计费业务能力。同时AI中台基于数据中台的用户信息、订购信息、历史消费信息、信用信息、流量等数据训练得出用户计算模型，数据中台根据计算模型形成控制策略、优先级等规则和具体的欠费风险、建议配额、订购等标签数据，通过定时、事件触发两种方式同步到业务中台，供计费请求实时匹配。','9','5','5','001','001','001','/images/5.1.1.4.5G数据业务智能计费.png','','','0','1','李海聪',NULL,'13102343214','李海聪',NULL,'李海聪');\n" +
                "insert into `bcoc_scene_info` (`index_id`, `scene_id`, `scene_version`, `scene_name_cn`, `scene_name_en`, `scene_desc`, `scene_menu_id`, `scene_star_level`, `scene_status`, `scene_area`, `scene_center`, `scene_type`, `scene_flowimage_path`, `scene_image_path`, `scene_url`, `scene_custom_tag`, `require_id`, `scene_create_user`, `scene_create_time`, `scene_create_user_phone`, `scene_update_user`, `scene_update_time`, `scene_update_user_phone`) values('28','10028','0.1','物联网新业务','sn_028','某企业找到客户经理希望开通物联网业务，客户经理从企业资质信息录入，合适的商业套餐推荐，直至服务开通的全过程管理。','9','3','5','001','001','001','/images/5.1.4.2.物联网新业务.png','','','0','1','李海聪',NULL,'13102343214','李海聪',NULL,'李海聪');";
        String[] split = sql.split("\\);");
        for (String s : split){
            dao.execute(s+")");
        }
    }
}
