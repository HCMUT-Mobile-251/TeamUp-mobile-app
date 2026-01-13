// 🧪 Quick test script to verify user data loading
// Chạy này trong browser console khi app đang chạy

// Copy đoạn này vào browser console:

(async () => {
  console.log("=== Testing User Data Loading ===\n");
  
  // 1. Check localStorage/SecureStore
  const token = localStorage.getItem("auth_token");
  const userId = localStorage.getItem("user_id");
  
  console.log("1️⃣ Storage Check:");
  console.log(`   Token: ${token ? '✅ Found' : '❌ Not found'}`);
  console.log(`   UserId: ${userId ? '✅ ' + userId : '❌ Not found'}\n`);
  
  if (!token || !userId) {
    console.log("❌ Missing token or userId. Please login first.");
    return;
  }
  
  // 2. Test API call
  console.log("2️⃣ Testing API call to GET /user/{userId}:");
  
  try {
    const API_BASE_URL = "http://localhost:8080"; // Change if needed
    const response = await fetch(`${API_BASE_URL}/user/${userId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    console.log(`   Status: ${response.status} ${response.statusText}`);
    
    const data = await response.json();
    console.log("   Response:", data);
    
    if (data.code === 200) {
      console.log("\n✅ SUCCESS! User data loaded:");
      console.log(`   - Name: ${data.result?.fullName}`);
      console.log(`   - Email: ${data.result?.email}`);
      console.log(`   - StudentId: ${data.result?.studentId}`);
      console.log(`   - Groups: ${data.result?.groups?.length || 0}`);
      console.log(`   - Tags: ${data.result?.userTags?.length || 0}`);
    } else {
      console.log(`\n❌ API Error: ${data.message}`);
    }
    
  } catch (error) {
    console.log(`\n❌ Network Error: ${error.message}`);
    console.log("   Make sure backend is running at:", API_BASE_URL);
  }
})();

