import request from '../utils/request'

export const getResorts = () => request.get('/user/resort/list')
export const getResortDetail = (id) => request.get(`/user/resort/${id}`)
export const getSkiRuns = () => request.get('/user/ski-run/list')
export const getSkiRunDetail = (id) => request.get(`/user/ski-run/${id}`)
export const getTickets = () => request.get('/user/ticket/list')
export const getEquipment = () => request.get('/user/equipment/list')
export const getCourses = () => request.get('/user/course/list')
export const getCoaches = () => request.get('/user/coach/list')
export const getFacilities = () => request.get('/user/facility/list')
export const getWeather = () => request.get('/user/weather/list')
export const getAnnouncements = () => request.get('/user/announcement/list')
export const getAnnouncementDetail = (id) => request.get(`/user/announcement/${id}`)

