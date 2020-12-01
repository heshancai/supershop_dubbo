package com.qf.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qf.IAddressService;
import com.qf.dao.AddressMapper;
import com.qf.entity.AddressEntity;
import com.qf.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AddressServiceImpl implements IAddressService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 根据用户id查询该用户所有的收获地址
     * @param uid
     * @return
     */
    @Override
    public List<AddressEntity> queryByUid(Integer uid) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("uid",uid);
        List<AddressEntity> list = addressMapper.selectList(queryWrapper);
        return list;
    }


    @Override
    public AddressEntity queryByAid(Integer id) {
        return addressMapper.selectById(id);
    }

    /**
     * 存储过程
     *
     *
     * 添加地址
     * @param addressEntity
     * @param user
     * @return
     */
    @Override
    public int insertAddress(AddressEntity addressEntity, User user) {

        QueryWrapper queryWrapper2=new QueryWrapper();
        queryWrapper2.eq("uid",user.getId());
        List<AddressEntity> list = addressMapper.selectList(queryWrapper2);

        if(list.size()>0){
            if(addressEntity.getIsdefault()==1){
                //为默认的收货地址  进行修改
                QueryWrapper queryWrapper=new QueryWrapper();
                queryWrapper.eq("uid",user.getId());
                queryWrapper.eq("isdefault",1);
                AddressEntity addressEntity1 = addressMapper.selectOne(queryWrapper);
                addressEntity1.setIsdefault(0);
                addressMapper.updateById(addressEntity);
            }
        }
        //判断是否是作为默认的收货地址
        addressEntity.setUid(user.getId());

        return addressMapper.insert(addressEntity);
    }
}
