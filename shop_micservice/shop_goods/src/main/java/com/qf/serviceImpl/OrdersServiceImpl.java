package com.qf.serviceImpl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.qf.IAddressService;
import com.qf.ICartService;
import com.qf.IOrdersService;
import com.qf.Utils.ShopUtil;
import com.qf.dao.OrderDetailMapper;
import com.qf.dao.OrdersMapper;
import com.qf.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrdersServiceImpl implements IOrdersService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Reference
    private IAddressService addressService;
    @Autowired
    private ICartService cartService;


    /**
     * 清空购物车
     * 下单
     * @param aid
     * @param cid
     * @param user
     * @return
     */
    @Override
    //事务管理
    @Transactional
    public OrdersEntity insertOrders(Integer aid, Integer[] cid, User user) {

        //根据aid获得收货地址得信息
        AddressEntity addressEntity = addressService.queryByAid(aid);

        //根据购物车cid获得购物车清单
        List<ShopCarEntity> shopCarEntities = cartService.queryCarByCid(cid);

        //计算总价
        double allPrice = ShopUtil.allPrice(shopCarEntities);
        //生成订单信息
        OrdersEntity ordersEntity=new OrdersEntity()
                .setAddress(addressEntity.getAddress())
                .setAllprice(BigDecimal.valueOf(allPrice))
                .setCode(addressEntity.getCode())
                .setUid(user.getId())
                .setOrderid(UUID.randomUUID().toString())
                .setPhone(addressEntity.getPhone())
                .setPerson(addressEntity.getPerson());
        //插入订单信息
        ordersMapper.insert(ordersEntity);

        //生成订单详情信息
        //一个购物车一个订单详情
        for (ShopCarEntity shopCarEntity : shopCarEntities) {
            OrderDetilsEntity orderDetilsEntity=new OrderDetilsEntity()
                    .setGid(shopCarEntity.getGid())
                    .setFmurl(shopCarEntity.getGoods().getFmurl())
                    .setNumber(shopCarEntity.getNumber())
                    .setOid(ordersEntity.getId())
                    .setDetilsPrice(shopCarEntity.getCartPrice())//商品小计
                    .setSubject(shopCarEntity.getGoods().getSubject())
                    .setPrice(shopCarEntity.getGoods().getPrice());//商品单价
        orderDetailMapper.insert(orderDetilsEntity);
        }
        //删除下单购物清单
        cartService.deletCarByCid(cid);
        return ordersEntity;
    }

    @Override
    public OrdersEntity queryByid(Integer id) {
        return  null;
    }

    /**
     * 根据用户id查询所有的订单
     * @param uid
     * @return
     */
    @Override
    public List<OrdersEntity> queryByUid(Integer uid) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("uid",uid);
        queryWrapper.orderByDesc("create_time");
        List<OrdersEntity> ordersEntities = ordersMapper.selectList(queryWrapper);
        //一个订单多个订单详情
        for (OrdersEntity ordersEntity : ordersEntities) {

            QueryWrapper queryWrapper1=new QueryWrapper();
            queryWrapper1.eq("oid",ordersEntity.getId());

            List<OrderDetilsEntity> list = orderDetailMapper.selectList(queryWrapper1);

            ordersEntity.setOrderDetils(list);

        }
        return ordersEntities;
    }


    @Override
    public OrdersEntity queryByOid(String oid) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("orderid",oid);
        OrdersEntity ordersEntity = ordersMapper.selectOne(queryWrapper);

        //查询订单详情
        QueryWrapper queryWrapper1=new QueryWrapper();
        queryWrapper1.eq("oid", ordersEntity.getId());
        List<OrderDetilsEntity> list = orderDetailMapper.selectList(queryWrapper1);
        ordersEntity.setOrderDetils(list);
        return ordersEntity;
    }

    @Override
    public int updateOrderStatus(String oid, Integer status) {
        OrdersEntity ordersEntity = this.queryByOid(oid);
        ordersEntity.setStatus(status);
        return ordersMapper.updateById(ordersEntity);
    }
}
