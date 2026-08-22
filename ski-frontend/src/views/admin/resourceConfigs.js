import {
  announcementCategories,
  coachLevels,
  facilityStatuses,
  facilityTypes,
  safetyStatuses,
  skiRunLevels,
  snowTypes,
  weatherTypes
} from '../../utils/dictionaries'

const enabledOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 }
]

const resortStatusOptions = [
  { label: '营业', value: 1 },
  { label: '停业', value: 0 }
]

const coachStatusOptions = [
  { label: '可预约', value: 1 },
  { label: '停用', value: 0 }
]

const genderOptions = ['男', '女'].map((value) => ({ label: value, value }))
const optionList = (items) => items.map((value) => ({ label: value, value }))

export const resourceConfigs = {
  resort: {
    title: '滑雪场管理',
    subtitle: '维护黑龙江省内合作滑雪场的地址、开放时间和营业状态。',
    createText: '新增滑雪场',
    entityName: '滑雪场',
    searchFields: [
      { prop: 'keyword', label: '关键词', type: 'keyword', keys: ['name', 'city', 'address'], placeholder: '名称、城市或地址' },
      { prop: 'status', label: '营业状态', type: 'select', options: resortStatusOptions }
    ],
    columns: [
      { prop: 'id', label: 'ID', width: 70 },
      { prop: 'name', label: '滑雪场名称', minWidth: 170 },
      { prop: 'city', label: '所在城市', width: 120 },
      { prop: 'address', label: '详细地址', minWidth: 210 },
      { prop: 'phone', label: '联系电话', width: 140 },
      { prop: 'openTime', label: '开放时间', width: 100, format: 'time' },
      { prop: 'closeTime', label: '闭场时间', width: 100, format: 'time' },
      { prop: 'image', label: '照片', width: 90, format: 'image' },
      { prop: 'status', label: '状态', width: 90, options: resortStatusOptions, tag: true,
        tagTypes: { 1: 'success', 0: 'info' } }
    ],
    defaults: { openTime: '08:00:00', closeTime: '17:00:00', status: 1 },
    formFields: [
      { prop: 'name', label: '滑雪场名称', required: true, maxlength: 100 },
      { prop: 'city', label: '所在城市', required: true, maxlength: 50, placeholder: '例如：哈尔滨市' },
      { prop: 'address', label: '详细地址', required: true, maxlength: 200, wide: true },
      { prop: 'phone', label: '联系电话', maxlength: 20, placeholder: '请输入固定电话或手机号' },
      { prop: 'status', label: '营业状态', type: 'select', required: true, options: resortStatusOptions },
      { prop: 'openTime', label: '开放时间', type: 'time', required: true },
      { prop: 'closeTime', label: '闭场时间', type: 'time', required: true },
      { prop: 'imageFile', label: '雪场照片', type: 'file', accept: 'image/*', wide: true,
        tip: '建议上传横向雪场实景图；编辑时未选择新图片会保留原图。' },
      { prop: 'introduction', label: '滑雪场简介', type: 'textarea', maxlength: 1000, wide: true }
    ]
  },

  skiRun: {
    title: '雪道管理',
    subtitle: '管理各滑雪场雪道的等级、长度、落差与展示图片。',
    createText: '新增雪道',
    entityName: '雪道',
    lookups: ['resorts'],
    searchFields: [
      { prop: 'keyword', label: '雪道名称', type: 'keyword', keys: ['name'], placeholder: '请输入雪道名称' },
      { prop: 'resortId', label: '所属滑雪场', type: 'select', lookup: 'resorts' },
      { prop: 'level', label: '难度等级', type: 'select', options: optionList(skiRunLevels) }
    ],
    columns: [
      { prop: 'id', label: 'ID', width: 70 },
      { prop: 'name', label: '雪道名称', minWidth: 160 },
      { prop: 'resortId', label: '所属滑雪场', minWidth: 170, lookup: 'resorts' },
      { prop: 'level', label: '等级', width: 95, options: optionList(skiRunLevels), tag: true,
        tagTypes: { 初级: 'success', 中级: 'primary', 高级: 'warning', 专家级: 'danger' } },
      { prop: 'length', label: '长度', width: 105, suffix: ' 米' },
      { prop: 'height', label: '落差', width: 105, suffix: ' 米' },
      { prop: 'image', label: '图片', width: 90, format: 'image' }
    ],
    formFields: [
      { prop: 'resortId', label: '所属滑雪场', type: 'select', required: true, lookup: 'resorts' },
      { prop: 'name', label: '雪道名称', required: true, maxlength: 100 },
      { prop: 'level', label: '难度等级', type: 'select', required: true, options: optionList(skiRunLevels) },
      { prop: 'length', label: '雪道长度（米）', type: 'number', required: true, min: 0, precision: 2 },
      { prop: 'height', label: '落差高度（米）', type: 'number', required: true, min: 0, precision: 2 },
      { prop: 'imageFile', label: '雪道图片', type: 'file', accept: 'image/*', wide: true,
        tip: '图片可不上传；编辑时未选择新图片会保留原图。' }
    ]
  },

  facility: {
    title: '设施管理',
    subtitle: '统一管理缆车、造雪机、服务大厅和救援设施的运行情况。',
    createText: '新增设施',
    entityName: '设施',
    lookups: ['resorts'],
    searchFields: [
      { prop: 'keyword', label: '关键词', type: 'keyword', keys: ['name', 'location'], placeholder: '设施名称或位置' },
      { prop: 'type', label: '设施类型', type: 'select', options: optionList(facilityTypes) },
      { prop: 'status', label: '运行状态', type: 'select', options: optionList(facilityStatuses) }
    ],
    columns: [
      { prop: 'id', label: 'ID', width: 70 },
      { prop: 'name', label: '设施名称', minWidth: 160 },
      { prop: 'resortId', label: '所属滑雪场', minWidth: 170, lookup: 'resorts' },
      { prop: 'type', label: '类型', width: 110 },
      { prop: 'location', label: '所在位置', minWidth: 150 },
      { prop: 'status', label: '状态', width: 100, options: optionList(facilityStatuses), tag: true,
        tagTypes: { 正常: 'success', 维护中: 'warning', 停用: 'info' } },
      { prop: 'lastCheckTime', label: '最近检查', width: 155, format: 'datetime' }
    ],
    defaults: { status: '正常' },
    formFields: [
      { prop: 'resortId', label: '所属滑雪场', type: 'select', required: true, lookup: 'resorts' },
      { prop: 'name', label: '设施名称', required: true, maxlength: 100 },
      { prop: 'type', label: '设施类型', type: 'select', required: true, options: optionList(facilityTypes) },
      { prop: 'status', label: '运行状态', type: 'select', required: true, options: optionList(facilityStatuses) },
      { prop: 'location', label: '所在位置', maxlength: 100 },
      { prop: 'lastCheckTime', label: '最近检查时间', type: 'datetime', defaultNow: true },
      { prop: 'description', label: '设施说明', type: 'textarea', maxlength: 500, wide: true }
    ]
  },

  ticket: {
    title: '雪票管理',
    subtitle: '管理不同雪道的票种、使用时长、价格和销售状态。',
    createText: '新增雪票',
    entityName: '雪票',
    lookups: ['skiRuns'],
    searchFields: [
      { prop: 'keyword', label: '票种名称', type: 'keyword', keys: ['name'], placeholder: '请输入票种名称' },
      { prop: 'skiRunId', label: '适用雪道', type: 'select', lookup: 'skiRuns' },
      { prop: 'status', label: '销售状态', type: 'select', options: enabledOptions }
    ],
    columns: [
      { prop: 'id', label: 'ID', width: 70 },
      { prop: 'name', label: '票种名称', minWidth: 170 },
      { prop: 'skiRunId', label: '适用雪道', minWidth: 160, lookup: 'skiRuns' },
      { prop: 'price', label: '价格', width: 105, format: 'money' },
      { prop: 'hour', label: '有效期', width: 100, suffix: ' 小时' },
      { prop: 'status', label: '状态', width: 90, options: enabledOptions, tag: true,
        tagTypes: { 1: 'success', 0: 'info' } },
      { prop: 'description', label: '使用说明', minWidth: 190 }
    ],
    defaults: { status: 1 },
    formFields: [
      { prop: 'name', label: '票种名称', required: true, maxlength: 100 },
      { prop: 'skiRunId', label: '适用雪道', type: 'select', required: true, lookup: 'skiRuns' },
      { prop: 'price', label: '票价（元）', type: 'number', required: true, min: 0, precision: 2,
        editDisabled: true, tip: '后端接口不允许在编辑时修改票价，如需调价请新建票种。' },
      { prop: 'hour', label: '有效期（小时）', type: 'number', required: true, min: 1, precision: 0 },
      { prop: 'status', label: '销售状态', type: 'select', required: true, options: enabledOptions,
        editDisabled: true, tip: '当前编辑接口只更新票种基础信息，状态仅在新增时设置。' },
      { prop: 'description', label: '使用说明', type: 'textarea', maxlength: 500, wide: true }
    ]
  },

  equipment: {
    title: '雪具管理',
    subtitle: '维护雪具租赁价格、库存、押金和超时费用。',
    createText: '新增雪具',
    entityName: '雪具',
    lookups: ['resorts'],
    searchFields: [
      { prop: 'keyword', label: '雪具名称', type: 'keyword', keys: ['name'], placeholder: '例如：双板套装' },
      { prop: 'resortId', label: '所属滑雪场', type: 'select', lookup: 'resorts' }
    ],
    columns: [
      { prop: 'id', label: 'ID', width: 70 },
      { prop: 'name', label: '雪具名称', minWidth: 160 },
      { prop: 'resortId', label: '所属滑雪场', minWidth: 170, lookup: 'resorts' },
      { prop: 'price', label: '日租金', width: 105, format: 'money' },
      { prop: 'total', label: '总库存', width: 90 },
      { prop: 'remaining', label: '可租数量', width: 95 },
      { prop: 'deposit', label: '押金', width: 100, format: 'money' },
      { prop: 'overtimeFee', label: '超时费', width: 100, format: 'money' }
    ],
    defaults: { total: 0, remaining: 0, overtimeFee: 0, deposit: 0 },
    formFields: [
      { prop: 'resortId', label: '所属滑雪场', type: 'select', required: true, lookup: 'resorts' },
      { prop: 'name', label: '雪具名称', required: true, maxlength: 100 },
      { prop: 'price', label: '每日租金（元）', type: 'number', required: true, min: 0, precision: 2 },
      { prop: 'deposit', label: '押金（元）', type: 'number', min: 0, precision: 2 },
      { prop: 'total', label: '总库存', type: 'number', required: true, min: 0, precision: 0 },
      { prop: 'remaining', label: '剩余可租数量', type: 'number', required: true, min: 0, precision: 0 },
      { prop: 'overtimeFee', label: '超时费用（元/小时）', type: 'number', min: 0, precision: 2 }
    ]
  },

  course: {
    title: '滑雪课程管理',
    subtitle: '安排课程内容、授课教练、教学雪道、时间和报名名额。',
    createText: '新增课程',
    entityName: '课程',
    lookups: ['coaches', 'skiRuns'],
    searchFields: [
      { prop: 'keyword', label: '课程名称', type: 'keyword', keys: ['title', 'teacher'], placeholder: '课程名称或教练' },
      { prop: 'skiRunId', label: '教学雪道', type: 'select', lookup: 'skiRuns' },
      { prop: 'level', label: '课程难度', type: 'select', options: optionList(skiRunLevels) }
    ],
    columns: [
      { prop: 'id', label: 'ID', width: 70 },
      { prop: 'title', label: '课程名称', minWidth: 170 },
      { prop: 'coachId', label: '授课教练', width: 120, lookup: 'coaches', fallbackProp: 'teacher' },
      { prop: 'skiRunId', label: '教学雪道', width: 140, lookup: 'skiRuns' },
      { prop: 'courseTime', label: '上课时间', width: 155, format: 'datetime' },
      { prop: 'hours', label: '时长', width: 85, suffix: ' 小时' },
      { prop: 'number', label: '剩余名额', width: 90 },
      { prop: 'price', label: '价格', width: 100, format: 'money' },
      { prop: 'level', label: '难度', width: 90 }
    ],
    formFields: [
      { prop: 'title', label: '课程名称', required: true, maxlength: 100 },
      { prop: 'coachId', label: '授课教练', type: 'select', required: true, lookup: 'coaches' },
      { prop: 'skiRunId', label: '教学雪道', type: 'select', required: true, lookup: 'skiRuns' },
      { prop: 'level', label: '课程难度', type: 'select', required: true, options: optionList(skiRunLevels) },
      { prop: 'courseTime', label: '上课时间', type: 'datetime', required: true, defaultNow: true },
      { prop: 'hours', label: '课程时长（小时）', type: 'number', required: true, min: 1, precision: 0 },
      { prop: 'number', label: '剩余名额', type: 'number', required: true, min: 0, precision: 0 },
      { prop: 'price', label: '课程价格（元）', type: 'number', required: true, min: 0, precision: 2 },
      { prop: 'content', label: '教学内容', type: 'textarea', maxlength: 500, wide: true }
    ]
  },

  coach: {
    title: '教练管理',
    subtitle: '维护滑雪教练的等级、资质、从业年限和预约状态。',
    createText: '新增教练',
    entityName: '教练',
    searchFields: [
      { prop: 'keyword', label: '关键词', type: 'keyword', keys: ['name', 'phone', 'certificate'], placeholder: '姓名、电话或证书' },
      { prop: 'level', label: '教练等级', type: 'select', options: optionList(coachLevels) },
      { prop: 'status', label: '预约状态', type: 'select', options: coachStatusOptions }
    ],
    columns: [
      { prop: 'id', label: 'ID', width: 70 },
      { prop: 'name', label: '教练姓名', minWidth: 130 },
      { prop: 'gender', label: '性别', width: 75 },
      { prop: 'phone', label: '联系电话', width: 140 },
      { prop: 'level', label: '教练等级', width: 110, tag: true },
      { prop: 'yearsOfExperience', label: '从业年限', width: 100, suffix: ' 年' },
      { prop: 'certificate', label: '资格证书', minWidth: 150 },
      { prop: 'status', label: '状态', width: 100, options: coachStatusOptions, tag: true,
        tagTypes: { 1: 'success', 0: 'info' } }
    ],
    defaults: { yearsOfExperience: 0, status: 1 },
    formFields: [
      { prop: 'name', label: '教练姓名', required: true, maxlength: 50 },
      { prop: 'gender', label: '性别', type: 'select', options: genderOptions },
      { prop: 'phone', label: '联系电话', maxlength: 20 },
      { prop: 'level', label: '教练等级', type: 'select', required: true, options: optionList(coachLevels) },
      { prop: 'yearsOfExperience', label: '从业年限', type: 'number', required: true, min: 0, precision: 0 },
      { prop: 'status', label: '预约状态', type: 'select', required: true, options: coachStatusOptions },
      { prop: 'certificate', label: '资格证书', maxlength: 100, wide: true },
      { prop: 'introduction', label: '教练简介', type: 'textarea', maxlength: 500, wide: true }
    ]
  },

  weather: {
    title: '天气雪况管理',
    subtitle: '按滑雪场和日期记录温度、风速、天气与雪质信息。',
    createText: '新增雪况',
    entityName: '天气雪况',
    lookups: ['resorts'],
    searchFields: [
      { prop: 'resortId', label: '所属滑雪场', type: 'select', lookup: 'resorts' },
      { prop: 'weather', label: '天气情况', type: 'select', options: optionList(weatherTypes) },
      { prop: 'snow', label: '雪质', type: 'select', options: optionList(snowTypes) }
    ],
    columns: [
      { prop: 'id', label: 'ID', width: 70 },
      { prop: 'resortId', label: '所属滑雪场', minWidth: 170, lookup: 'resorts' },
      { prop: 'date', label: '日期', width: 110, format: 'date' },
      { prop: 'temperatureMin', label: '最低温', width: 100, suffix: ' ℃' },
      { prop: 'temperatureMax', label: '最高温', width: 100, suffix: ' ℃' },
      { prop: 'weather', label: '天气', width: 90, tag: true },
      { prop: 'wind', label: '风速', width: 105, suffix: ' m/s' },
      { prop: 'snow', label: '雪质', width: 90 }
    ],
    formFields: [
      { prop: 'resortId', label: '所属滑雪场', type: 'select', required: true, lookup: 'resorts' },
      { prop: 'date', label: '日期', type: 'date', required: true, defaultToday: true },
      { prop: 'temperatureMin', label: '最低温度（℃）', type: 'number', required: true, precision: 2, min: -60, max: 40 },
      { prop: 'temperatureMax', label: '最高温度（℃）', type: 'number', required: true, precision: 2, min: -60, max: 40 },
      { prop: 'weather', label: '天气情况', type: 'select', required: true, options: optionList(weatherTypes) },
      { prop: 'snow', label: '雪质', type: 'select', required: true, options: optionList(snowTypes) },
      { prop: 'wind', label: '风速（m/s）', type: 'number', required: true, min: 0, precision: 2 }
    ]
  },

  announcement: {
    title: '公告管理',
    subtitle: '发布雪道、票务、天气、开放时间和课程等运营通知。',
    createText: '发布公告',
    entityName: '公告',
    searchFields: [
      { prop: 'keyword', label: '关键词', type: 'keyword', keys: ['title', 'content'], placeholder: '标题或公告内容' },
      { prop: 'category', label: '公告分类', type: 'select', options: optionList(announcementCategories) }
    ],
    columns: [
      { prop: 'id', label: 'ID', width: 70 },
      { prop: 'title', label: '公告标题', minWidth: 210 },
      { prop: 'category', label: '分类', width: 110, tag: true },
      { prop: 'content', label: '公告内容', minWidth: 300 },
      { prop: 'publishTime', label: '发布时间', width: 155, format: 'datetime' }
    ],
    formFields: [
      { prop: 'title', label: '公告标题', required: true, maxlength: 150, wide: true },
      { prop: 'category', label: '公告分类', type: 'select', required: true, options: optionList(announcementCategories) },
      { prop: 'publishTime', label: '发布时间', type: 'datetime', required: true, defaultNow: true },
      { prop: 'content', label: '公告内容', type: 'textarea', required: true, maxlength: 2000, rows: 6, wide: true }
    ]
  },

  maintenance: {
    title: '设施维护记录',
    subtitle: '记录设施维护人员、时间、内容、费用和处理结果。',
    createText: '新增维护记录',
    entityName: '维护记录',
    lookups: ['facilities', 'employees'],
    searchFields: [
      { prop: 'facilityId', label: '维护设施', type: 'select', lookup: 'facilities' },
      { prop: 'employeeId', label: '维护人员', type: 'select', lookup: 'employees' },
      { prop: 'keyword', label: '维护内容', type: 'keyword', keys: ['content', 'result'], placeholder: '内容或结果' }
    ],
    columns: [
      { prop: 'id', label: 'ID', width: 70 },
      { prop: 'facilityId', label: '维护设施', minWidth: 170, lookup: 'facilities' },
      { prop: 'employeeId', label: '维护人员', width: 120, lookup: 'employees' },
      { prop: 'maintenanceTime', label: '维护时间', width: 155, format: 'datetime' },
      { prop: 'content', label: '维护内容', minWidth: 220 },
      { prop: 'cost', label: '维护费用', width: 110, format: 'money' },
      { prop: 'result', label: '维护结果', minWidth: 160 }
    ],
    defaults: { cost: 0 },
    formFields: [
      { prop: 'facilityId', label: '维护设施', type: 'select', required: true, lookup: 'facilities' },
      { prop: 'employeeId', label: '维护人员', type: 'select', required: true, lookup: 'employees' },
      { prop: 'maintenanceTime', label: '维护时间', type: 'datetime', required: true, defaultNow: true },
      { prop: 'cost', label: '维护费用（元）', type: 'number', required: true, min: 0, precision: 2 },
      { prop: 'content', label: '维护内容', type: 'textarea', required: true, maxlength: 500, wide: true },
      { prop: 'result', label: '维护结果', type: 'textarea', maxlength: 200, wide: true }
    ]
  },

  safetyInspection: {
    title: '安全巡检管理',
    subtitle: '记录雪道巡检人员、雪况、安全结论、问题和处理措施。',
    createText: '新增巡检记录',
    entityName: '巡检记录',
    lookups: ['skiRuns', 'employees'],
    searchFields: [
      { prop: 'skiRunId', label: '巡检雪道', type: 'select', lookup: 'skiRuns' },
      { prop: 'employeeId', label: '巡检人员', type: 'select', lookup: 'employees' },
      { prop: 'safetyStatus', label: '安全状态', type: 'select', options: optionList(safetyStatuses) }
    ],
    columns: [
      { prop: 'id', label: 'ID', width: 70 },
      { prop: 'skiRunId', label: '巡检雪道', minWidth: 150, lookup: 'skiRuns' },
      { prop: 'employeeId', label: '巡检人员', width: 120, lookup: 'employees' },
      { prop: 'inspectionTime', label: '巡检时间', width: 155, format: 'datetime' },
      { prop: 'snowCondition', label: '雪面情况', minWidth: 130 },
      { prop: 'safetyStatus', label: '安全状态', width: 100, options: optionList(safetyStatuses), tag: true,
        tagTypes: { 安全: 'success', 需处理: 'warning', 已关闭: 'info' } },
      { prop: 'problem', label: '发现问题', minWidth: 200 },
      { prop: 'action', label: '处理措施', minWidth: 200 }
    ],
    defaults: { safetyStatus: '安全' },
    formFields: [
      { prop: 'skiRunId', label: '巡检雪道', type: 'select', required: true, lookup: 'skiRuns' },
      { prop: 'employeeId', label: '巡检人员', type: 'select', required: true, lookup: 'employees' },
      { prop: 'inspectionTime', label: '巡检时间', type: 'datetime', required: true, defaultNow: true },
      { prop: 'safetyStatus', label: '安全状态', type: 'select', required: true, options: optionList(safetyStatuses) },
      { prop: 'snowCondition', label: '雪面情况', maxlength: 50, wide: true, placeholder: '例如：压雪平整、局部结冰' },
      { prop: 'problem', label: '发现问题', type: 'textarea', maxlength: 500, wide: true },
      { prop: 'action', label: '处理措施', type: 'textarea', maxlength: 500, wide: true }
    ]
  }
}

export const getResourceConfig = (resource) => resourceConfigs[resource]
