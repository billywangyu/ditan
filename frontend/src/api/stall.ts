import axios from 'axios'

const api = axios.create({ baseURL: '/api' })

export interface StallRequest {
  title: string
  category: string
  longitude: number
  latitude: number
  address: string
}

export interface StallResponse {
  id: number
  title: string
  category: string
  longitude: number
  latitude: number
  address: string
  status: string
  expireAt: string
  createdAt: string
}

export const stallApi = {
  createStall(data: StallRequest) {
    return api.post<StallResponse>('/stalls', data)
  },
  getNearbyStalls(lng: number, lat: number, radius = 5000) {
    return api.get<StallResponse[]>('/stalls/nearby', {
      params: { lng, lat, radius }
    })
  }
}