export interface Building {
  buildingId: number
  buildingName: string
  buildingNo: string
  floorCount: number
  unitCount: number
  buildingArea: number
  buildingAddress: string
  description: string
  status: '0' | '1'
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
}

export interface BuildingForm {
  buildingId?: number
  buildingName: string
  buildingNo: string
  floorCount: number
  unitCount: number
  buildingArea: number
  buildingAddress: string
  description: string
  status: '0' | '1'
}

export interface BuildingQuery {
  pageNum: number
  pageSize: number
  buildingName?: string
  buildingNo?: string
  status?: string
}