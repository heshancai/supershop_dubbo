package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.qf.IGoodsService;
import com.qf.entity.GoodsEntity;
import com.qf.entity.ResultData;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    //设置图片的上传的路径存在本地磁盘中
    String uploadPath="C:/worker/imgs";

    @Reference
    IGoodsService goodsService;

    //注入对象是呀fastdfs
    @Autowired
    private FastFileStorageClient fileStorageClient;


    @RequestMapping("/list")
    public String list(Model model){
        List<GoodsEntity> list = goodsService.list();
        model.addAttribute("list",list);
        return "goodslist";
    }


    //查询参与分页
    @RequestMapping("/pagelist")
    public String pageList(Model model,Integer current){

        if(current==null || current<0){
            current=1;
        }
        //构建分页的条件 当前页 展示的记录条数
        Page<GoodsEntity> page=new Page<>(current,5);
        Page<GoodsEntity> pagelist=goodsService.listPage(page);
        System.out.println("当前页"+pagelist.getCurrent());
        System.out.println("每页展示的记录数:"+pagelist.getSize());
        System.out.println("总页数:"+pagelist.getPages());
        System.out.println("总记录数:"+pagelist.getTotal());
        model.addAttribute("pageInfo",pagelist);
        model.addAttribute("list",pagelist.getRecords());
        return "goodslist1";
    }



    //上传一个图片
    @RequestMapping("/uploader")
    @ResponseBody
    public ResultData<String> uploader(MultipartFile file) {
        System.out.println("触发了图片的上传"+file.getOriginalFilename());


        //上传到fastdfs
        String fullPath=null;
        try {
            StorePath storePath = fileStorageClient.uploadImageAndCrtThumbImage(file.getInputStream(),
                    file.getSize(),"jpg",null);

             fullPath = storePath.getFullPath();
            //得到访问图片的路径
            System.out.println("上传的结果:"+storePath.getFullPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        //准备图片的名称
//        String imgName= UUID.randomUUID().toString();
//
//        String path=uploadPath+"/"+imgName;

        //构建流文件
//        try (InputStream in=file.getInputStream();
//             OutputStream ou=new FileOutputStream(path);
//        ){
//            //文件读写操作 文件进行保存
//            IOUtils.copy(in,ou);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //返回图片的绝对路径到前台
        return  new ResultData<String>().setCode(ResultData.ResultCodeList.OK).setData("http://www.he.com:8080/"+fullPath);
    }

    //图片的回显
    @RequestMapping("/showimg")
    public void showImage(String imgPath, HttpServletResponse response){

        System.out.println(imgPath);
            //读取前端传过来的绝对路径本地读取输入流
        try (InputStream inputStream=new FileInputStream(imgPath);
             //构建输出流
             ServletOutputStream outputStream=response.getOutputStream();
        ){
            //文件的读写操作
            IOUtils.copy(inputStream,outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //添加商品的信息
    @RequestMapping("/insert")
    public String insert(GoodsEntity goodsEntity){
        goodsService.insert(goodsEntity);
        return "redirect:/goods/pagelist";

    }

    //删除商品
    @RequestMapping("/delete/{gid}")
    public String deletOne(@PathVariable String gid){
        Integer id = Integer.valueOf(gid);
        //删除商品表数据 商品图片表的数据
        goodsService.delete(id);
        return "redirect:/goods/pagelist";
    }




}
