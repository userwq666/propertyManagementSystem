export interface DictType {
  dictId: number
  dictName: string
  dictType: string
  status: '0' | '1'
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
}

export interface DictTypeQueryParams {
  pageNum: number
  pageSize: number
  dictName?: string
  dictType?: string
  status?: string
  beginTime?: string
  endTime?: string
}

export interface DictTypeFormData {
  dictId?: number
  dictName: string
  dictType: string
  status: '0' | '1'
  remark?: string
}

export interface DictTypeListResponse {
  rows: DictType[]
  total: number
}

export interface DictTypeOption {
  dictId: number
  dictName: string
  dictType: string
}

// 字典数据
export interface DictData {
  dictCode: number
  dictSort: number
  dictLabel: string
  dictValue: string
  dictType: string
  cssClass: string
  listClass: string
  isDefault: 'Y' | 'N'
  status: '0' | '1'
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
}

export interface DictDataQueryParams {
  pageNum: number
  pageSize: number
  dictLabel?: string
  dictValue?: string
  status?: string
  dictType?: string
  beginTime?: string
  endTime?: string
}

export interface DictDataFormData {
  dictCode?: number
  dictSort: number
  dictLabel: string
  dictValue: string
  dictType: string
  cssClass?: string
  listClass?: string
  isDefault: 'Y' | 'N'
  status: '0' | '1'
  remark?: string
}

export interface DictDataListResponse {
  rows: DictData[]
  total: number
}