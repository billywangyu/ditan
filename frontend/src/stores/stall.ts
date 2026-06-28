import { defineStore } from 'pinia'
import { stallApi, StallResponse } from '@/api/stall'

export const useStallStore = defineStore('stall', {
  state: () => ({
    stalls: [] as StallResponse[],
    loading: false
  }),
  actions: {
    async fetchNearby(lng: number, lat: number) {
      this.loading = true
      try {
        const res = await stallApi.getNearbyStalls(lng, lat)
        this.stalls = res.data
      } finally {
        this.loading = false
      }
    },
    async addStall(data: Parameters<typeof stallApi.createStall>[0]) {
      await stallApi.createStall(data)
    }
  }
})