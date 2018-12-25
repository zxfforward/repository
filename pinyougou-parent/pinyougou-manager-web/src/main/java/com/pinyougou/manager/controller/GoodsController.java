package com.pinyougou.manager.controller;

import java.util.List;

import com.alibaba.fastjson.JSON;

import com.pinyougou.pojo.TbItem;


import entity.Goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	@Autowired
	private Destination queueSolrDestination;//用于发送 solr 导入的消息(点对点)
	@Autowired
	private Destination queueSolrDeleteDestination;//用于发送 solr 删除的消息
	@Autowired
	private Destination topicPageDestination;//用于生成商品详细页的消息目标(发布订阅)
	@Autowired
	private Destination topicPageDeleteDestination;//用于删除商品详细页的消息目标(发布订阅)
	@Autowired
	private JmsTemplate jmsTemplate;//引入模板
	/*@Reference
	private SearchService searchService;*/
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	@RequestMapping("updateStatus")
	public Result updateStatus(Long ids[], String status) {
		Result result = new Result();
		try {
			goodsService.updateStatus(ids, status);
			result.setSuccess(true);
			result.setMessage("提交成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage("提交失败");
			return result;
		}

		//如果商品审核通过把对应的商品项添加到索引库
		try {
			if ("2".equals(status)) {
				List<TbItem> itemList = goodsService.findItemListByGoodsIdListAndStatus(ids,status);
				  final String jsonString = JSON.toJSONString(itemList);//将json对象转成json字符串，便于使用text信息发送消息
				  jmsTemplate.send(queueSolrDestination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {

						return session.createTextMessage(jsonString);
					}
				});
				//searchService.importItemList(itemList);
				//****生成商品详细页
				for(final Long goodsId:ids){
					//itemPageService.genItemHtml(goodsId);
					jmsTemplate.send(topicPageDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(goodsId+"");
						}
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("更新索引库失败");
		}

		return result;
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);
			//删除索引库
			//searchService.deleteByGoodsIds(Arrays.asList(ids));
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					//ids数组用对象信息传输
					return session.createObjectMessage(ids);
				}
			});
            //删除每个服务器上的商品详情页
			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){

//		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
//		goods.setSellerId(sellerId);
		return goodsService.findPage(goods, page, rows);		
	}
	/*@Reference(timeout=40000)
	private ItemPageService itemPageService;

	@RequestMapping("/genHtml")
	public void genHtml(Long goodsId){

		itemPageService.genItemHtml(goodsId);

	}*/
}
