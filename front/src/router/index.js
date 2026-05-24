import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import LoginPage from '../components/LoginPage.vue'
import OverviewPage from '../components/OverviewPage.vue'
import RoomPage from '../components/RoomPage.vue'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginPage,
    meta: { public: true }
  },
  {
    path: '/',
    name: 'overview',
    component: OverviewPage
  },
  {
    path: '/room/:id',
    name: 'room',
    component: RoomPage,
    props: true
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  if (to.meta.public) {
    next()
  } else if (!auth.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router
