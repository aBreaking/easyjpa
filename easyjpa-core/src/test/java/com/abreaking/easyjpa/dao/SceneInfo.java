package com.abreaking.easyjpa.dao;
;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 表 bcoc_scene_info
 * 
 * @author liwei_paas
 * @date 2020-12-14
 */
@Table(name = "bcoc_scene_info")
public class SceneInfo
{
	private static final long serialVersionUID = 1L;
	
	/** ID */
	@Id
	private Long indexId;
	/** 场景编码 */
	private String sceneId;
	/**  */
	private String sceneVersion;
	/** 场景中文名称 */
	private String sceneNameCn;
	/** 场景英文名称 */
	private String sceneNameEn;
	/** 场景描述 */
	private String sceneDesc;
	/** 场景归属目录ID */
	private Long sceneMenuId;
	/** 场景星级 */
	private Integer sceneStarLevel;
	/** 能力状态: 0 注销 1 开发中 2 测试中 3 审核中 4 待上架（审核通过和下架包含在这里） 5 已上架  */
	private Integer sceneStatus;
	/** 场景归属业务域 */
	private String sceneArea;
	/** 场景归属中心 */
	private String sceneCenter;
	/** 场景业务类型 */
	private String sceneType;
	/** 流程图片路径 */
	private String sceneFlowimagePath;
	/** 原型图片路径 */
	private String sceneImagePath;
	/** 访问URL */
	private String sceneUrl;
	/** 场景自定义标签 */
	private String sceneCustomTag;
	/** 需求编码 */
	private String requireId;
	/** 场景创建人 */
	private String sceneCreateUser;
	/** 场景创建时间 */
	private Date sceneCreateTime;
	/** 场景创建人联系方式 */
	private String sceneCreateUserPhone;
	/** 场景更新人 */
	private String sceneUpdateUser;
	/** 场景更新时间 */
	private Date sceneUpdateTime;
	/** 场景更新人联系方式 */
	private String sceneUpdateUserPhone;

	public void setIndexId(Long indexId) 
	{
		this.indexId = indexId;
	}

	public Long getIndexId() 
	{
		return indexId;
	}
	public void setSceneId(String sceneId) 
	{
		this.sceneId = sceneId;
	}

	public String getSceneId() 
	{
		return sceneId;
	}
	public void setSceneVersion(String sceneVersion) 
	{
		this.sceneVersion = sceneVersion;
	}

	public String getSceneVersion() 
	{
		return sceneVersion;
	}
	public void setSceneNameCn(String sceneNameCn) 
	{
		this.sceneNameCn = sceneNameCn;
	}

	public String getSceneNameCn() 
	{
		return sceneNameCn;
	}
	public void setSceneNameEn(String sceneNameEn) 
	{
		this.sceneNameEn = sceneNameEn;
	}

	public String getSceneNameEn() 
	{
		return sceneNameEn;
	}
	public void setSceneDesc(String sceneDesc) 
	{
		this.sceneDesc = sceneDesc;
	}

	public String getSceneDesc() 
	{
		return sceneDesc;
	}
	public void setSceneMenuId(Long sceneMenuId) 
	{
		this.sceneMenuId = sceneMenuId;
	}

	public Long getSceneMenuId() 
	{
		return sceneMenuId;
	}

	public Integer getSceneStarLevel() {
		return sceneStarLevel;
	}

	public void setSceneStarLevel(Integer sceneStarLevel) {
		this.sceneStarLevel = sceneStarLevel;
	}

	public void setSceneStatus(Integer sceneStatus)
	{
		this.sceneStatus = sceneStatus;
	}

	public Integer getSceneStatus() 
	{
		return sceneStatus;
	}
	public void setSceneArea(String sceneArea) 
	{
		this.sceneArea = sceneArea;
	}

	public String getSceneArea() 
	{
		return sceneArea;
	}
	public void setSceneCenter(String sceneCenter) 
	{
		this.sceneCenter = sceneCenter;
	}

	public String getSceneCenter() 
	{
		return sceneCenter;
	}
	public void setSceneType(String sceneType) 
	{
		this.sceneType = sceneType;
	}

	public String getSceneType() 
	{
		return sceneType;
	}
	public void setSceneFlowimagePath(String sceneFlowimagePath) 
	{
		this.sceneFlowimagePath = sceneFlowimagePath;
	}

	public String getSceneFlowimagePath() 
	{
		return sceneFlowimagePath;
	}
	public void setSceneImagePath(String sceneImagePath) 
	{
		this.sceneImagePath = sceneImagePath;
	}

	public String getSceneImagePath() 
	{
		return sceneImagePath;
	}
	public void setSceneUrl(String sceneUrl) 
	{
		this.sceneUrl = sceneUrl;
	}

	public String getSceneUrl() 
	{
		return sceneUrl;
	}
	public void setSceneCustomTag(String sceneCustomTag) 
	{
		this.sceneCustomTag = sceneCustomTag;
	}

	public String getSceneCustomTag() 
	{
		return sceneCustomTag;
	}
	public void setRequireId(String requireId) 
	{
		this.requireId = requireId;
	}

	public String getRequireId() 
	{
		return requireId;
	}
	public void setSceneCreateUser(String sceneCreateUser) 
	{
		this.sceneCreateUser = sceneCreateUser;
	}

	public String getSceneCreateUser() 
	{
		return sceneCreateUser;
	}
	public void setSceneCreateTime(Date sceneCreateTime) 
	{
		this.sceneCreateTime = sceneCreateTime;
	}

	public Date getSceneCreateTime() 
	{
		return sceneCreateTime;
	}
	public void setSceneCreateUserPhone(String sceneCreateUserPhone) 
	{
		this.sceneCreateUserPhone = sceneCreateUserPhone;
	}

	public String getSceneCreateUserPhone() 
	{
		return sceneCreateUserPhone;
	}
	public void setSceneUpdateUser(String sceneUpdateUser) 
	{
		this.sceneUpdateUser = sceneUpdateUser;
	}

	public String getSceneUpdateUser() 
	{
		return sceneUpdateUser;
	}
	public void setSceneUpdateTime(Date sceneUpdateTime) 
	{
		this.sceneUpdateTime = sceneUpdateTime;
	}

	public Date getSceneUpdateTime() 
	{
		return sceneUpdateTime;
	}
	public void setSceneUpdateUserPhone(String sceneUpdateUserPhone) 
	{
		this.sceneUpdateUserPhone = sceneUpdateUserPhone;
	}

	public String getSceneUpdateUserPhone() 
	{
		return sceneUpdateUserPhone;
	}

	@Override
	public String toString() {
		return "SceneInfo{" +
				"indexId=" + indexId +
				", sceneId='" + sceneId + '\'' +
				", sceneNameCn='" + sceneNameCn + '\'' +
				", sceneCreateUser='" + sceneCreateUser + '\'' +
				", sceneCreateTime=" + sceneCreateTime +
				", sceneCreateUserPhone='" + sceneCreateUserPhone + '\'' +
				'}';
	}
}
