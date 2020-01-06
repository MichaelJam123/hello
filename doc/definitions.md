# 模型定义

## <a id="error"></a>Error
服务端返回的错误信息

#### 属性定义

属性名|数据类型|必填|属性说明
---|:---|:---:|:---
code|string|N|错误代码
message|string|N|错误描述

## <a id="attend_detailed"></a>AttendDetailed


#### 属性定义

属性名|数据类型|必填|属性说明
---|:---|:---:|:---
adId|integer|Y|
adTime|string|Y|
adNumber|integer|N|
adStatus|integer|N|
adDifferentplaces|integer|N|
adLate|integer|N|
adEarly|integer|N|
adEmp|integer|N|
adTask|integer|N|
adAttend|integer|N|

## <a id="attend_info"></a>AttendInfo


#### 属性定义

属性名|数据类型|必填|属性说明
---|:---|:---:|:---
attendId|integer|Y|
attendTime|string|N|
attendAddress|string|N|
empAttend|integer|N|
taskAttend|integer|N|

## <a id="employee_info"></a>EmployeeInfo


#### 属性定义

属性名|数据类型|必填|属性说明
---|:---|:---:|:---
employeeId|integer|Y|
employeeUsername|string|N|
employeePassword|string|N|
employeeName|string|N|
employeeSex|string|N|
employeeDept|string|N|
employeeEmail|string|N|
employeeTime|string|N|
employeeStatus|integer|N|
employeeNumber|string|N|
employeePosition|string|N|

## <a id="task_info"></a>TaskInfo


#### 属性定义

属性名|数据类型|必填|属性说明
---|:---|:---:|:---
taskId|integer|Y|
taskName|string|N|
taskStartTime|string|N|
taskStartOver|string|N|
taskEndTime|string|N|
taskEndOver|string|N|
taskCreateTime|string|N|
taskStatus|integer|N|
taskType|string|N|
taskAddress|string|N|
taskCreate|integer|N|

