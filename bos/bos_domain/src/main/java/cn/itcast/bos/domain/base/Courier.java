package cn.itcast.bos.domain.base;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import lombok.Data;

/**
 * @description:快递员
 */
@Entity
@Data
@Table(name = "T_COURIER")
public class Courier {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "C_ID")
	private Integer id; // 主键
	@Column(name = "C_COURIER_NUM", unique = true)
	private String courierNum; // 快递员工号
	@Column(name = "C_NAME")
	private String name; // 快递员姓名
	@Column(name = "C_TELEPHONE")
	private String telephone; // 快递员联系电话
	@Column(name = "C_PDA")
	private String pda; // PDA号
	@Column(name = "C_DELTAG")
	private Character deltag; // 作废标志 1 为标记作废
	@Column(name = "C_CHECK_PWD")
	private String checkPwd; // 查台密码
	@Column(name = "C_TYPE")
	private String type; // 取件员类型
	@Column(name = "C_COMPANY")
	private String company; // 单位
	@Column(name = "C_VEHICLE_TYPE")
	private String vehicleType; // 车辆类型
	@Column(name = "C_VEHICLE_NUM")
	private String vehicleNum; // 车牌号

	@ManyToOne
	@JoinColumn(name = "C_STANDARD_ID")
	private Standard standard;

	@ManyToOne
	@JoinColumn(name = "C_TAKETIME_ID")
	private TakeTime takeTime;

	@ManyToMany(mappedBy = "couriers")
	private Set<FixedArea> fixedAreas = new HashSet<>();

}
