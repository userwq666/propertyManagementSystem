export interface Parking {
  parkingId: number
  parkingNo: string
  buildingId: number
  buildingName: string
  unitNo: string
  parkingNum: string
  parkingType: '1' | '2' | '3' | '4'
  parkingTypeLabel: string
  parkingStatus: '1' | '2' | '3' | '4'
  parkingStatusLabel: string
  monthlyRent: number
  deposit: number
  houseId?: number
  houseNo?: string
  ownerName?: string
  ownerPhone?: string
  status: '0' | '1'
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
}

export interface ParkingForm {
  parkingId?: number
  parkingNo: string
  buildingId: number
  unitNo: string
  parkingNum: string
  parkingType: '1' | '2' | '3' | '4'
  parkingStatus: '1' | '2' | '3' | '4'
  monthlyRent: number
  deposit: number
  houseId?: number
  ownerId?: number
  status: '0' | '1'
  remark?: string
}

export interface ParkingQuery {
  pageNum: number
  pageSize: number
  parkingNo?: string
  buildingId?: number
  parkingType?: string
  parkingStatus?: string
  houseId?: number
  ownerName?: string
  ownerPhone?: string
  status?: string
}

export interface ParkingRent {
  rentId: number
  parkingId: number
  parkingNo: string
  buildingName: string
  ownerId: number
  ownerName: string
  ownerPhone: string
  startDate: string
  endDate: string
  rentAmount: number
  deposit: number
  payStatus: string
  payStatusLabel: string
  createTime: string
}

export interface BuildingTreeNode {
  buildingId: number
  buildingName: string
  buildingNo: string
  children?: BuildingTreeNode[]
}

export interface HouseTreeNode {
  houseId: number
  houseNo: string
  buildingName: string
  unitNo: string
  roomNo: string
  label: string
  value: number
  children?: HouseTreeNode[]
}

export interface ParkingOwnerBind {
  parkingId: number
  ownerId: number
  ownerName: string
  ownerPhone: string
  startDate: string
  endDate: string
  rentAmount: number
  deposit: number
}