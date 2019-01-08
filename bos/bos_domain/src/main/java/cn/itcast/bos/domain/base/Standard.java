package cn.itcast.bos.domain.base;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * @description:收派标准
 */
@Entity
@Data
@Table(name = "T_STANDARD")
public class Standard implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "C_ID")
	protected Integer id; // 主键
	@Column(name = "C_NAME")
	private String name; // 标准名称
	@Column(name = "C_MIN_WEIGHT")
	private Integer minWeight; // 最小重量
	@Column(name = "C_MAX_WEIGHT")
	private Integer maxWeight; // 最大重量
	@Column(name = "C_MIN_LENGTH")
	private Integer minLength; // 最小长度
	@Column(name = "C_MAX_LENGTH")
	private Integer maxLength; // 最大重量
	@Column(name = "C_OPERATING_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date operatingTime;// 操作时间
	@Column(name = "C_OPERATOR")
	private String operator; // 操作员
	@Column(name = "C_OPERATING_COMPANY")
	private String operatingCompany; // 操作单位

}
