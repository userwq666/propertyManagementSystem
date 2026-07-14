# 澄清问题

任务描述提到“使用jakarta.validation进行参数校验（兼容Spring Boot 4.x）”，但提供的实体类代码中没有使用这些注解。

请确认：
1. 是否需要在实体类字段上添加jakarta.validation注解（如@NotNull, @NotBlank等）？
2. 如果需要，哪些字段应该添加什么注解？
3. 或者校验逻辑应该在服务层或DTO中进行？