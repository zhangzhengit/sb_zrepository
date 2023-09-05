
# 使用说明：
# 一  springboot 项目引入maven依赖后可以直接使用，已封装为starter形式
	
	1 新springboot项目 A ，引入
		
			<dependency>
				<groupId>com.vo</groupId>
				<artifactId>sb_zrepository</artifactId>
				<version>0.0.1-SNAPSHOT</version>
			</dependency>
	
	2 resources目录下新建 zdatasource.properties 
	  配置详见文件 zdatasource.properties
		
	3 声明一个Entity，如：
	
		@Data
		@AllArgsConstructor
		@NoArgsConstructor
		@ZEntity(tableName = "user")
		public class UserEntity {
		
			@ZID
			private Long id;
		
			private Integer orderCount;
		
			private String name;
		
			private Integer age;
		
			private Integer status;
		
		}
	
	4 声明 UserEntity 的 UserRepository，此接口需要继承ZRepository接口,
	
		public interface UserRepository extends com.vo.ZRepository<UserEntity, Long> {
			
			// 支持声明式方法，如下：
			// 参数类型和名称需要 与 UserEntity 中匹配
			List<UserEntity> findByName(String name);
			Long countingByAge(Integer age);
			List<UserEntity> findByNameLike(String name);
			List<UserEntity> findByStatusOrderByIdDescLimit(Long status, int offset, int rows);
			..........
			..........
		
		}
		
		到此，UserRepository 接口已继承 ZRepository 中固定的一些方法，
		支持的声明式方法详见：MethodRegex中的GROUP_开头的正则表达式.
		
	5 @Autowired  UserRepository userRepository;
	  即可使用 UserRepository 中声明式方法和 ZRepository 中的固有方法
	  
	6 事务：注解 @ZTransaction
		在需要事务的方法上加上此注解接口实现事务
		
# 二 其他使用,调用  ZRepositoryStarter.startZRepository(扫描的包名)， 
	得到Map<Class,ZClass> 为Map<ZRepository子接口的Class，其代理类的ZClass>。自行处理
	
				
	  
  
	
	