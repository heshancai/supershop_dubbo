package com.qf.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qf.ICartService;
import com.qf.IGoodsService;
import com.qf.dao.ShopCarMapper;
import com.qf.entity.GoodsEntity;
import com.qf.entity.ShopCarEntity;
import com.qf.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private ShopCarMapper shopCarMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    //注入商品服务的对象
    @Autowired
    private IGoodsService goodsService;
    //添加商品到购物车
    @Override
    public String insertCar(ShopCarEntity shopCarEntity, User user, String cartToken) {

        //根据商品的id查询商品的详情信息
        GoodsEntity goodsEntity = goodsService.queryById(shopCarEntity.getGid());
        //计算商品的小计
        BigDecimal cartPrice = goodsEntity.getPrice().multiply(BigDecimal.valueOf(shopCarEntity.getNumber()));
        shopCarEntity.setCartPrice(cartPrice);

        //判断是否登录
        if(user!=null){
            //已经登录保存到数据库中
            shopCarEntity.setUid(user.getId());
            shopCarMapper.insert(shopCarEntity);
        }else {
            //未登录
            //存到redis链表中 链式存储
            cartToken=cartToken !=null? cartToken: UUID.randomUUID().toString();
            //链表从左放入 后进先出
            redisTemplate.opsForList().leftPush(cartToken,shopCarEntity);

            //同一商品的数量的改变判断
            //全部取出list
            //遍历list
            //修改某个购物车的数量，将这个购物车商品提到最上面
            //清空所有list
            //将当前list写入redis

        }
        return cartToken;
    }

    /**
     * 查询用户的购物车的信息
     * 登录的和未登录的
     * @param cartToken
     * @param user
     * @return
     */
    @Override
    public List<ShopCarEntity> listCarts(String cartToken, User user) {
        List<ShopCarEntity> shopCarEntities=null;
        if(user!=null){
            //已经登录 查询数据库的信息
            QueryWrapper queryWrapper=new QueryWrapper();
            //根据用户的id查询购物车的消息
            queryWrapper.eq("uid",user.getId());
            //根据时间降序查询
            queryWrapper.orderByDesc("create_time");
            //条件查询
            shopCarEntities=shopCarMapper.selectList(queryWrapper);
        }else {
            //未登录 查询redis中的购物车的信息
            if(cartToken!=null){
                //查询长度
                Long size = redisTemplate.opsForList().size(cartToken);
                //得到该长度的所有的商品
                shopCarEntities=redisTemplate.opsForList().range(cartToken,0,size);
            }
        }

        //关联查询商品的信息
        for (ShopCarEntity shopCarEntity: shopCarEntities){
            //获得商品的id
            Integer gid = shopCarEntity.getGid();
            GoodsEntity goodsEntity = goodsService.queryById(gid);
            shopCarEntity.setGoods(goodsEntity);
        }
        return shopCarEntities;
    }

    /**
     * 根据商品的id和用户的di查询购物车的信息
     * @param gid
     * @param user
     * @return
     */
    @Override
    public List<ShopCarEntity> queryCarByGid(Integer[] gid, User user) {

        QueryWrapper queryWrapper=new QueryWrapper();
        //查询条件
        queryWrapper.eq("uid",user.getId());
        queryWrapper.in("gid",gid);
        List<ShopCarEntity> list = shopCarMapper.selectList(queryWrapper);
        //商品的信息进行关联
        for (ShopCarEntity shopCarEntity:list){
            Integer gid1 = shopCarEntity.getGid();
            GoodsEntity goodsEntity = goodsService.queryById(gid1);
            shopCarEntity.setGoods(goodsEntity);

        }
        return list;
    }

    /**
     * 根据购物车的id查询购物车的信息
     * @param cids
     * @return
     */
    @Override
    public List<ShopCarEntity> queryCarByCid(Integer[] cids) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.in("id",cids);
        List<ShopCarEntity> list = shopCarMapper.selectList(queryWrapper);
        for (ShopCarEntity shopCarEntity:list){
            Integer gid1 = shopCarEntity.getGid();
            GoodsEntity goodsEntity = goodsService.queryById(gid1);
            shopCarEntity.setGoods(goodsEntity);
        }
        return list;
    }

    /**
     * 根据购物车的id删除购物车的信息
     * @param cid
     * @return
     */
    @Override
    public int deletCarByCid(Integer[] cid) {
        return shopCarMapper.deleteBatchIds(Arrays.asList(cid));
    }
}
