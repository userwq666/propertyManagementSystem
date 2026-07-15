export interface House {
  houseId: number
  houseNo: string
  buildingId: number
  buildingName: string
  unitNo: string
  roomNo: string
  floorNum: number
  houseType: '1' | '2' | '3' | '4'
  houseTypeLabel: string
  buildArea: number
  innerArea: number
  orientation: string
  decorationStatus: '1' | '2' | '3' | '4'
  decorationStatusLabel: string
  houseStructure: string
  status: '0' | '1'
  ownerName: string
  ownerPhone: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
}

export interface HouseForm {
  houseId?: number
  houseNo: string
  buildingId: number
  unitNo: string
  roomNo: string
  floorNum: number
  houseType: '1' | '2' | '3' | '4'
  buildArea: number
  innerArea: number
  orientation: string
  decorationStatus: '1' | '2' | '3' | '4'
  houseStructure: string
  status: '0' | '1'
}

export interface HouseQuery {
  pageNum: number
  pageSize: number
  houseNo?: string
  buildingId?: number
  houseType?: string
  decorationStatus?: string
  ownerName?: string
  ownerPhone?: string
  status?: string
}

export interface HouseOwner {
  ownerId: number
  ownerName: string
  ownerPhone: string
  idCard: string
  relationType: string
  isMain: 'Y' | 'N'
}

export interface BuildingTreeNode {
  buildingId: number
  buildingName: string
  buildingNo: string
  children?: BuildingTreeNode[]
}