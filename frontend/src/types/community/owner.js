export interface Owner {
  ownerId: number
  ownerName: string
  ownerPhone: string
  idCard: string
  gender: '1' | '2'
  genderLabel: string
  birthDate?: string
  age: number
  email: string
  emergencyContact: string
  emergencyPhone: string
  idType: string
  idTypeLabel: string
  idNumber?: string
  idCardFrontUrl: string
  idCardBackUrl: string
  status: '0' | '1'
  statusLabel: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
  houses?: OwnerHouse[]
}

export interface OwnerForm {
  ownerId?: number
  ownerName: string
  ownerPhone: string
  idCard: string
  gender: '1' | '2'
  birthDate?: string
  age?: number
  email?: string
  emergencyContact?: string
  emergencyPhone?: string
  idType?: string
  idNumber?: string
  idCardFrontUrl?: string
  idCardBackUrl?: string
  status: '0' | '1'
  remark?: string
  houseIds?: number[]
}

export interface OwnerQuery {
  pageNum: number
  pageSize: number
  ownerName?: string
  ownerPhone?: string
  idCard?: string
  houseId?: number
  houseNo?: string
  status?: string
}

export interface OwnerHouse {
  houseId: number
  houseNo: string
  buildingName: string
  unitNo: string
  roomNo: string
  relationType: string
  isMain: 'Y' | 'N'
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