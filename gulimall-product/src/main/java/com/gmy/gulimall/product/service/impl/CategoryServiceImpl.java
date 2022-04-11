package com.gmy.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gmy.gulimall.product.service.CategoryBrandRelationService;
import com.gmy.gulimall.product.vo.Catalogs2Vo;
import io.netty.util.internal.StringUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.Query;

import com.gmy.gulimall.product.dao.CategoryDao;
import com.gmy.gulimall.product.entity.CategoryEntity;
import com.gmy.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 以树形结构获取商品分类
     *
     * @return list集合
     */
    @Override
    public List<CategoryEntity> getAllCategoryWithTree() {

        // 获取所有的分类
        final List<CategoryEntity> categorys = this.baseMapper.selectList(null);
        // 获取一级分类
        final List<CategoryEntity> level1Menus = categorys.stream()
                // 过滤出 根菜单
                .filter(category -> category.getParentCid() == 0)
                // 找出所有的子菜单
                .map(menu -> {
                    menu.setChildren(getChildren(menu, categorys));
                    return menu;
                })
                // 排序
                .sorted(Comparator.comparingInt(menu ->
                        menu.getSort() == null ? 0 : menu.getSort())
                )
                // 收集
                .collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO: 1、检查当前删除的菜单，是否被别的地方引用

        this.baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        ArrayList<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);

        return parentPath.toArray(new Long[0]);
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        // 收集当前节点Id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 递归查询当前菜单的子菜单
     *
     * @param root 当前菜单
     * @param all  所有的菜单
     * @return 获取当前菜单的子菜单
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        final List<CategoryEntity> children = all.stream()
                .filter(categoryEntity -> {
                    return categoryEntity.getParentCid().equals(root.getCatId());
                })
                // 递归找子菜单
                .map(categoryEntity -> {
                    categoryEntity.setChildren(getChildren(categoryEntity, all));
                    return categoryEntity;
                })
                .sorted(Comparator.comparingInt(menu ->
                        menu.getSort() == null ? 0 : menu.getSort()))
                .collect(Collectors.toList());

        return children;
    }

    /**
     * 级联更新 分类名
     *
     * @param category 分类的实体
     */
    @Override
    public void updateCascade(CategoryEntity category) {
        this.baseMapper.updateById(category);

        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    public List<CategoryEntity> getLevel1Categories() {

        LambdaQueryWrapper<CategoryEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryEntity::getParentCid, 0);
        return this.baseMapper.selectList(wrapper);
    }


    /**
     * 查询数据库中的分类
     *
     * @return 分类集合
     */
    private Map<String, List<Catalogs2Vo>> getCatalogDataFromDB() {
        // 查询数据库的时候，先查询缓存，缓存无数据再去查询
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        // 缓存中有
        if (!StringUtil.isNullOrEmpty(catalogJSON)) {
            // 直接返回
            return JSON.parseObject(catalogJSON,
                    new TypeReference<Map<String, List<Catalogs2Vo>>>() {
                    });
        }

        System.out.println("查询了数据库");
        // 性能优化：将数据库的多次查询变为一次
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);

        //1、查出所有分类
        //1、1）查出所有一级分类
        List<CategoryEntity> level1Categories = this.getParentCid(selectList, 0L);

        // 封装数据
        Map<String, List<Catalogs2Vo>> parentCid = level1Categories.stream()
                .collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                    //1、每一个的一级分类,查到这个一级分类的二级分类
                    List<CategoryEntity> categoryEntities = this.getParentCid(selectList, v.getCatId());

                    //2、封装上面的结果
                    List<Catalogs2Vo> catalogs2Vos = null;
                    if (categoryEntities != null) {
                        catalogs2Vos = categoryEntities.stream().map(l2 -> {
                            Catalogs2Vo catalogs2Vo = new Catalogs2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName().toString());

                            //1、找当前二级分类的三级分类封装成vo
                            List<CategoryEntity> level3Catelog = this.getParentCid(selectList, l2.getCatId());

                            if (level3Catelog != null) {
                                List<Catalogs2Vo.Category3Vo> category3Vos = level3Catelog.stream().map(l3 -> {
                                    //2、封装成指定格式
                                    Catalogs2Vo.Category3Vo category3Vo = new Catalogs2Vo.Category3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());

                                    return category3Vo;
                                }).collect(Collectors.toList());
                                catalogs2Vo.setCatalog3List(category3Vos);
                            }

                            return catalogs2Vo;
                        }).collect(Collectors.toList());
                    }

                    return catalogs2Vos;
                }));

        // 2.数据放到缓存中存的数据都是 json 字符串
        String catalogJson = JSON.toJSONString(parentCid);
        // 3.设置过期时间,防止缓存雪崩
        redisTemplate.opsForValue().set("catalogJSON", catalogJson, 1, TimeUnit.DAYS);

        return parentCid;
    }

    /**
     * 数据库查询并封装整个分类 加本地锁
     *
     * @return 集合
     */
    @Override
    public Map<String, List<Catalogs2Vo>> getCatalogJsonFromDBWithLocalLock() {

        // 本地🔒
        synchronized (this) {
            return this.getCatalogDataFromDB();
        }
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList, Long parentCid) {
        return selectList.stream().
                filter(item -> item.getParentCid().equals(parentCid))
                .collect(Collectors.toList());
    }

    /**
     * TODO：可能产生 OutOfDirectMemoryError 异常
     * 使用 redis 来缓冲
     *
     * @return 分类结果
     */
    @Override
    public Map<String, List<Catalogs2Vo>> getCatalogJsonFromRedis() {

        /**
         *  1、空结果缓存，解决缓存穿透问题
         *  2、设置过期时间，+随机植，缓存雪崩问题
         *  3、加锁，解决缓存击穿问题
         */
        // 加入缓存逻辑 任何数据 存放在 redis 里都 json 字符串
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        // 缓存中没有
        if (StringUtil.isNullOrEmpty(catalogJSON)) {
            // 1.查数据库
            this.getCatalogJsonFromDBWithLocalLock();
        }

        // 逆转为指定的对象
        Map<String, List<Catalogs2Vo>> result = JSON.parseObject(catalogJSON,
                new TypeReference<Map<String, List<Catalogs2Vo>>>() {
                });

        return result;
    }


    public Map<String, List<Catalogs2Vo>> getCatalogJsonFromDBWithRedisLock() {
        // 占分布式锁,redis 占坑,并且设置过期时间 原子操作
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue()
                .setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(lock)) {
            System.out.println("获取分布式锁成功...");
            // 加锁成功。。。执行业务
            Map<String, List<Catalogs2Vo>> catalogDataFromDB;
            try {
                catalogDataFromDB = this.getCatalogDataFromDB();
            } finally {
                // 获取值对比 + 对比成功删除=原子操作 使用 Lua 脚本
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('get',KEYS[1]) else return 0 end";
                Integer lock1 = redisTemplate.execute(new DefaultRedisScript<>(script, Integer.class),
                        Collections.singletonList("lock"), uuid);
            }

            // 删除锁
            // 先去redis查询下保证当前的锁是自己的
            // 获取值对比，对比成功删除=原子性 lua脚本解锁
            // String lockValue = stringRedisTemplate.opsForValue().get("lock");
            // if (uuid.equals(lockValue)) {
            //     //删除我自己的锁
            //     stringRedisTemplate.delete("lock");

            return catalogDataFromDB;
        } else {
            // 加锁失败...重新试一试
            try {
                // 休眠 100ms
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return this.getCatalogJsonFromDBWithRedisLock();
        }
    }

    /**
     * 缓存里边和数据库里边如何保持数据的一致性
     * 1。双写模式
     * 2。失效模式
     * @return 分类
     */
    @Override
    public Map<String, List<Catalogs2Vo>> getCatalogJsonFromDBWithRedissonLock() {

        // 锁的名字，锁的粒度，越细越快
        RLock lock = redissonClient.getLock("CatalogJson-lock");
        lock.lock();

        // 加锁成功。。。执行业务
        Map<String, List<Catalogs2Vo>> catalogDataFromDB;
        try {
            catalogDataFromDB = this.getCatalogDataFromDB();
        } finally {
            lock.unlock();
        }


        return catalogDataFromDB;

    }


}