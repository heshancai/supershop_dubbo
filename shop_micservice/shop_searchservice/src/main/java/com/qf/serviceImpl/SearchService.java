package com.qf.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.ISearchService;
import com.qf.entity.GoodsEntity;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchService implements ISearchService {

    //注入操作searchService的对象
    @Autowired
    private SolrClient solrClient;
    //添加数据到索引库
    @Override
    public int insertSolr(GoodsEntity goodsEntity) {

        //实例化document对象
        SolrInputDocument document=new SolrInputDocument();
        //documen对象中添加索引库对应的属性值 数据库中id为string类型
        document.setField("id",goodsEntity.getId()+"");
        document.setField("subject",goodsEntity.getSubject());
        document.setField("info",goodsEntity.getInfo());
        document.setField("price",goodsEntity.getPrice().doubleValue());
        //库存
        document.setField("save",goodsEntity.getSave());
        //图片的地址
        document.setField("image",goodsEntity.getFmurl());


        //将对象添加到solrclietn中
        try {
            solrClient.add(document);
            //增删改的操作必须进行提交
            solrClient.commit();
            return 1;
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    //根据关键字查询所有的库的信息
    @Override
    public List<GoodsEntity> querySolr(String keyword) {
        System.out.println("搜索的关键字为:"+keyword);

        //设置搜索的条件
        SolrQuery solrQuery=new SolrQuery();
        //对象设置搜索的关键字
        if(keyword!=null && !keyword.equals("")){
            //设置进行名字或者商品的描述字段进行搜索
            solrQuery.setQuery("subject:"+keyword+" || info:"+keyword);

        }else{
            //无条件搜索
            solrQuery.setQuery("*:*");
        }


        //对搜索出来的进行分页的设置
        //开始搜索的索引
        solrQuery.setStart(0);
        //每次搜索出来10条信息
        solrQuery.setRows(10);

        //设置搜索的高亮
        //开启高亮
        solrQuery.setHighlight(true);

        //设置高亮的前缀  添加前缀的元素
        solrQuery.setHighlightSimplePre("<font color='red'>");
        //设置高亮的后缀
        solrQuery.setHighlightSimplePost("</font>");
        //添加需要设置高亮的字段
        solrQuery.addHighlightField("subject");

        //高亮内容得折叠前三个内容取出来
        //商城
        //solrQuery.setHighlightSnippets(3);
        //高亮关键字出现的前后十个字符截取出来
        //solrQuery.setHighlightFragsize(10);

        try {
            //将携带条件的对象进行搜索
            //得到响应的结果集
            QueryResponse queryResponse = solrClient.query(solrQuery);
            //结果集中得到高亮的结果集
            //map<当前高亮字段对应的对象的id,高亮的信息>
            //高亮的信息:Map<String, List<String>>
            //<高亮的字段，高亮内容的集合>
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();

            //获得搜索的结果 得到一个集合ArrayList
            SolrDocumentList results = queryResponse.getResults();
            //符合关键字的总记录条数
            long numFound = results.getNumFound();//可能返回的 结果1千万条分页限制显示10条

            //构建一个集合来装搜索出来的结果
            List<GoodsEntity> list=new ArrayList<>();
            //循环进行添加到集合中
            for (SolrDocument document:results){
                //document对象信息赋值到good对象中
                GoodsEntity goods=new GoodsEntity();
                //通过key的值得到对应的values
                goods.setId(Integer.valueOf((String) document.get("id")));
                goods.setSubject(document.get("subject")+"")
                        .setSave((Integer) document.get("save"))
                        .setPrice(BigDecimal.valueOf((double) document.get("price")))
                        .setFmurl(document.get("image")+"");

                //处理高亮的结果集
                //判断当前的对象的id是否存在高亮的信息
                if(highlighting.containsKey(goods.getId()+"")){
                    //说明当前的商品存在高亮的信息
                    //依次获取高亮的字段
                    //通过对应的key值进行获取map集合中的信息
                    Map<String, List<String>> stringListMap = highlighting.get(goods.getId() + "");
                    //通过设置高亮的字段进行获取高亮的信息
                    List<String> subject = stringListMap.get("subject");
                    //从集合中获取第一位进行存储覆盖
                    if(subject!=null){
                        String s = subject.get(0);
                        goods.setSubject(s);
                    }

                }
                //将当前对象的信息添加到集合中
                list.add(goods);
            }
            return list;
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
