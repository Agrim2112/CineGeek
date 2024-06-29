package com.example

import android.content.Context
import android.content.Context.*
import android.net.Uri
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.ApiService
import com.example.api.FCMApi
import com.example.di.Resource
import com.example.models.ApiResponse
import com.example.models.Chat
import com.example.models.ChatList
import com.example.models.UserFavouriteModel
import com.example.models.MovieCast
import com.example.models.MovieDetails
import com.example.models.MovieFavourites
import com.example.models.MovieImages
import com.example.models.Movies
import com.example.models.NotificationBody
import com.example.models.ReceiverChatList
import com.example.models.SendMessageDto
import com.example.models.UserModel
import com.example.models.UserToken
import com.example.repository.FCMRepository
import com.example.repository.MoviesRepository
import com.example.utils.Constants
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel
@Inject constructor(private val moviesRepository: MoviesRepository, private val fcmRepository:FCMRepository): ViewModel() {
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("favourites")
    var userName =""
    private var chatListEventListener: ValueEventListener? = null
    private var userFavEventListener:ValueEventListener? = null


    val errorMessage = MutableLiveData<String>()

    private val isFavourite = MutableLiveData<Boolean>()
    val isFavouriteResponse: LiveData<Boolean>
        get() = isFavourite

    private val MatchUserList = MutableLiveData<List<UserModel>>()
    val MatchUserListResponse: LiveData<List<UserModel>>
        get() = MatchUserList

    private val getUser = MutableLiveData<UserModel>()
    val getUserResponse: LiveData<UserModel>
        get() = getUser

    private val getChatList = MutableLiveData<List<ReceiverChatList>>()
    val chatListResponse: LiveData<List<ReceiverChatList>>
        get() = getChatList

    private val getChats = MutableLiveData<List<Chat>>()
    val getChatsResponse: LiveData<List<Chat>>
        get() = getChats

    private val signUp = MutableLiveData<ApiResponse>()
    val signUpResponse: LiveData<ApiResponse>
        get() = signUp

    private val fetchFavourites = MutableLiveData<List<String>>()
    val fetchFavouritesResponse: LiveData<List<String>>
        get() = fetchFavourites

    private val fetchCommonFavourites = MutableLiveData<List<String>>()
    val fetchCommonFavouritesResponse: LiveData<List<String>>
        get() = fetchCommonFavourites

    private val fetchPopularMovies = MutableLiveData<Resource<Movies>>()
    val popularMoviesResponse: LiveData<Resource<Movies>>
        get() = fetchPopularMovies

    private val fetchTopRatedMovies = MutableLiveData<Resource<Movies>>()
    val topRatedMoviesResponse: LiveData<Resource<Movies>>
        get() = fetchTopRatedMovies

    private val fetchUpcomingMovies = MutableLiveData<Resource<Movies>>()
    val upcomingMoviesResponse: LiveData<Resource<Movies>>
        get() = fetchUpcomingMovies

    private val fetchMovieDetails = MutableLiveData<MovieDetails>()
    val movieDetailsResponse: LiveData<MovieDetails>
        get() = fetchMovieDetails

    private val fetchCommonMovieDetails = MutableLiveData<MovieDetails>()
    val commonMoviesDetailsResponse: LiveData<MovieDetails>
        get() = fetchCommonMovieDetails

    private val fetchSimilarMoviesList = MutableLiveData<Movies>()

    val similarMovieListResponse: LiveData<Movies>
        get() = fetchSimilarMoviesList

    private val fetchMovieCast = MutableLiveData<MovieCast>()
    val movieCastResponse: LiveData<MovieCast>
        get() = fetchMovieCast

    private val fetchMovieImages = MutableLiveData<MovieImages>()
    val movieImagesResponse: LiveData<MovieImages>
        get() = fetchMovieImages

    private val fetchSearchResults = MutableLiveData<Movies>()

    val searchResultsReponse: LiveData<Movies>
        get() = fetchSearchResults

    init {
        getPopularMovies()
        getTopRatedMovies()
        getUpcomingMovies()
    }


    fun signUp(userModel: UserModel) {
        viewModelScope.launch {
            try {
                val response = fcmRepository.signUp(userModel)
                when (response.code()) {
                    201 -> {
                        val uid = response.body()?.uid
                        Log.d("Sign Up",uid!!)
                        signUp.postValue(response.body())
                    }
                    400 -> {
                        val errorJson = JSONObject(response.errorBody()?.string())
                        val error = errorJson.getString("error")
                        Log.d("Sign Up",error)
                        errorMessage.postValue(error)
                    }
                    else -> {
                    }
                }
            } catch (e: Exception) {
                Log.d("Server Error", e.message.toString())
            }
        }
    }
    private fun getPopularMovies() {
        viewModelScope.launch {
            try {
                fetchPopularMovies.value = Resource.loading()
                val response = moviesRepository.getPopularMovies()
                if (response.isSuccessful) {
                    fetchPopularMovies.value = Resource.success(response.body()!!)
                } else {
                    fetchPopularMovies.value = Resource.empty()
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    fetchPopularMovies.value = Resource.offlineError()
                } else {
                    fetchPopularMovies.value = Resource.error(e)
                }
            }
        }
    }

    private fun getTopRatedMovies() {
        viewModelScope.launch {
            try {
                fetchTopRatedMovies.value = Resource.loading()
                val response = moviesRepository.getTopRatedMovies()
                if (response.isSuccessful) {
                    fetchTopRatedMovies.value = Resource.success(response.body()!!)
                } else {
                    fetchTopRatedMovies.value = Resource.empty()
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    fetchTopRatedMovies.value = Resource.offlineError()
                } else {
                    fetchTopRatedMovies.value = Resource.error(e)
                }
            }
        }
    }

    private fun getUpcomingMovies() {
        viewModelScope.launch {
            try {
                fetchUpcomingMovies.value = Resource.loading()
                val response = moviesRepository.getUpcomingMovies()
                if (response.isSuccessful) {
                    fetchUpcomingMovies.value = Resource.success(response.body()!!)
                } else {
                    fetchUpcomingMovies.value = Resource.empty()
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    fetchUpcomingMovies.value = Resource.offlineError()
                } else {
                    fetchUpcomingMovies.value = Resource.error(e)
                }
            }
        }
    }

    fun getMovieDetails(movieId: String) {
        viewModelScope.launch {
            moviesRepository.getMovieDetails(movieId).let { response ->
                if (response.isSuccessful) {
                    fetchMovieDetails.postValue(response.body())
                } else {
                    Log.d("MoviesViewModel", "getMovieDetails: ${response.errorBody()}")
                }
            }
        }
    }

    fun getCommonMovieDetails(movieId: String) {
        viewModelScope.launch {
            moviesRepository.getMovieDetails(movieId).let { response ->
                if (response.isSuccessful) {
                    fetchCommonMovieDetails.postValue(response.body())
                } else {
                    Log.d("MoviesViewModel", "getMovieDetails: ${response.errorBody()}")
                }
            }
        }
    }


    fun getSimilarMovies(movieId: String) {
        viewModelScope.launch {
            moviesRepository.getSimilarMovies(movieId).let { response ->
                if (response.isSuccessful) {
                    fetchSimilarMoviesList.postValue(response.body())
                } else {
                    Log.d("MoviesViewModel", "getSimilarMovies:${response.errorBody()}")
                }
            }
        }
    }

    fun getMovieCast(movieId: String) {
        viewModelScope.launch {
            moviesRepository.getMovieCast(movieId).let { response ->
                if (response.isSuccessful) {
                    fetchMovieCast.postValue(response.body())
                } else {
                    Log.d("MoviesViewModel", "getMovieCast:${response.errorBody()}")
                }
            }
        }
    }

    fun getMovieImages(movieId: String) {
        viewModelScope.launch {
            moviesRepository.getMovieImages(movieId).let { response ->
                if (response.isSuccessful) {
                    fetchMovieImages.postValue(response.body())
                } else {
                    Log.d("MoviesViewModel", "getMovieImages:${response.errorBody()}")
                }
            }
        }
    }

    fun getSearchResults(search: String) {
        viewModelScope.launch {
            moviesRepository.getSearchResults(search).let { response ->
                if (response.isSuccessful) {
                    fetchSearchResults.postValue(response.body())
                } else {
                    Log.d("MoviesViewModel", "getSearchResults;${response.errorBody()}")
                }
            }
        }
    }

    fun addToFavourites(userID: String, movieId: String) {
        viewModelScope.launch {
            val userFav = database.getReference("UserFavourites").child(userID)
            val movieFav = database.getReference("MovieFavourites").child(movieId)

            userFav.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentMovies =
                        snapshot.getValue(UserFavouriteModel::class.java)?.movieId ?: listOf()
                    if (!currentMovies.contains(movieId)) {
                        val updatedMovies = currentMovies + movieId
                        userFav.setValue(UserFavouriteModel(updatedMovies))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }

            })

            movieFav.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentUsers =
                        snapshot.getValue(MovieFavourites::class.java)?.userId ?: listOf()
                    if (!currentUsers.contains(userID)) {
                        val updatedUsers = currentUsers + userID
                        movieFav.setValue(MovieFavourites(updatedUsers))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }

            })
        }
    }

    fun removeFromFavourites(userID: String, movieId: String) {
        viewModelScope.launch {
            val userFav = database.getReference("UserFavourites").child(userID)
            val movieFav = database.getReference("MovieFavourites").child(movieId)

            userFav.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentMovies =
                        snapshot.getValue(UserFavouriteModel::class.java)?.movieId ?: listOf()
                    val updatedMovies = currentMovies.filter { it != movieId }
                    userFav.setValue(UserFavouriteModel(updatedMovies))
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }

            })

            movieFav.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentUsers =
                        snapshot.getValue(MovieFavourites::class.java)?.userId ?: listOf()
                    val updatedUsers = currentUsers.filter { it != userID }
                    movieFav.setValue(MovieFavourites(updatedUsers))
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }

            })
        }
    }

    fun getFavourites(userID: String) {
        viewModelScope.launch {
            val userFav = database.getReference("UserFavourites").child(userID)
            userFavEventListener=object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userFavouriteModel = snapshot.getValue(UserFavouriteModel::class.java)
                    val movieIds = userFavouriteModel?.movieId ?: listOf()
                    fetchFavourites.postValue(movieIds)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }
            }

            userFav?.addValueEventListener(userFavEventListener!!)
        }
    }

    fun getCommonFavourites(userID: String) {
        viewModelScope.launch {
            val userFav = database.getReference("UserFavourites").child(userID)
            userFavEventListener=object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userFavouriteModel = snapshot.getValue(UserFavouriteModel::class.java)
                    val movieIds = userFavouriteModel?.movieId ?: listOf()
                    fetchCommonFavourites.postValue(movieIds)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }
            }

            userFav?.addValueEventListener(userFavEventListener!!)
        }
    }

    fun checkFavourites(movieId: String) {
        viewModelScope.launch {
            val movieFav = database.getReference("UserFavourites").child(currentUser!!)

            movieFav.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userFavouriteModel = snapshot.getValue(UserFavouriteModel::class.java)
                    val movieIds = userFavouriteModel?.movieId ?: listOf()
                    isFavourite.postValue(movieIds?.contains(movieId) ?: false)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }
            })
        }
    }

    fun getUserMatchList(movieId: String) {
        viewModelScope.launch {
            val movieFav = database.getReference("MovieFavourites").child(movieId)
            movieFav.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = snapshot.getValue(MovieFavourites::class.java)
                    val userListIds = users?.userId

                    if (userListIds != null) {
                        val userList = mutableListOf<UserModel>()
                        for (user in userListIds) {
                            val userId = database.getReference("Users").child(user)
                            userId.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val userData = snapshot.getValue(UserModel::class.java)
                                    if (userData?.uid != FirebaseAuth.getInstance().currentUser?.uid)
                                        userList.add(userData!!)
                                    MatchUserList.postValue(userList)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }
            })
        }
    }

    fun getUser(userID: String) {
        val userReference = database.getReference("Users").child(userID)
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)!!
                getUser.postValue(user!!)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MoviesViewModel", "onCancelled: ${error.message}")
            }

        })
    }

    fun sendMessage(receiverId: String, message: String, url: String = "") {
        val chatReference = database.getReference("Chat")
        val messageId = database.reference.push().key
        val tokenReference = database.getReference("UserTokens").child(receiverId)

        val userReference = database.getReference("Users").child(currentUser!!)
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usernameCurrent = snapshot.getValue(UserModel::class.java)!!
                userName=usernameCurrent?.name!!
                tokenReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val token = snapshot.getValue(String::class.java)
                        Log.d("token",token!!)
                        viewModelScope.launch {

                            val pushMessage=SendMessageDto(token, NotificationBody(userName,message))
//                            fcmRepository.sendMessage(pushMessage)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MoviesViewModel", "onCancelled: ${error.message}")
            }

        })



        chatReference
            .child(messageId!!)
            .setValue(
                Chat(
                    FirebaseAuth.getInstance().currentUser?.uid!!,
                    message,
                    receiverId,
                    false,
                    url,
                    messageId
                )
            )
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val chatListReference = database.getReference("ChatList")
                        .child(currentUser!!)
                        .child(receiverId)

                    chatListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(!snapshot.exists()){
                                chatListReference.child("isTyping").setValue(false)
                            }
                            chatListReference.child("id").setValue(ChatList(messageId,System.currentTimeMillis()))
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })

                    val chatListReceiverReference = database.getReference("ChatList")
                        .child(receiverId)
                        .child(currentUser)
                    chatListReceiverReference.addListenerForSingleValueEvent(object:ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(!snapshot.exists()){
                                chatListReceiverReference.child("isTyping").setValue(false)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })

                    chatListReceiverReference.child("id").setValue(ChatList(messageId,System.currentTimeMillis()))

                    val reference = database.reference.child("Users").child(currentUser)
                }
            }
    }

    fun sendImageMessage(receiverId: String, data: Uri) {
        val storageReference = FirebaseStorage.getInstance().reference.child("Chat Image")
        val messageId = database.reference.push().key
        val filePath = storageReference.child("$messageId.png")
        val uploadTask = filePath.putFile(data)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            if (!it.isSuccessful) {
                it.exception?.let {
                    throw it
                }
            }

            return@Continuation filePath.downloadUrl
        })
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val downloadUrl = it.result
                    val url = downloadUrl.toString()

                    val chatReference = database.getReference("Chat")
                    val messageId = database.reference.push().key

                    chatReference
                        .child(messageId!!)
                        .setValue(
                            Chat(
                                FirebaseAuth.getInstance().currentUser?.uid!!,
                                "Image",
                                receiverId,
                                false,
                                url,
                                messageId
                            )
                        )
                }
            }
            .addOnFailureListener {
                Log.e("ViewModel", it.message.toString())
            }
    }

    fun getChat(senderId: String, receiverId: String) {
        viewModelScope.launch {
            val chatReference = database.getReference("Chat")
            val chatList = mutableListOf<Chat>()

            chatReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatList.clear()
                    for (chat in snapshot.children) {
                        val chats = chat.getValue(Chat::class.java)

                        if (chat != null && (chats?.receiver == senderId && chats?.sender == receiverId) || (chats?.receiver == receiverId && chats?.sender == senderId)) {
                            chatList.add(chats!!)
                        }
                    }
                    getChats.postValue(chatList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }

            })
        }


    }

    fun getChatList() {
        val chatList = mutableListOf<ReceiverChatList>()
        val chatListReference = database.getReference("ChatList").child(currentUser!!)
        chatListEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (chat in snapshot.children) {
                    chatList.clear()
                    val user = chat.key.toString()
                    val message = chat.child("id").getValue(ChatList::class.java)
                    val isTyping = chat.child("isTyping").getValue(Boolean::class.java)
                    Log.d("isTyping",isTyping.toString())
                    val lastMessage = message?.id

                    val userReference = database.getReference("Users").child(user)
                    userReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userInfo = snapshot.getValue(UserModel::class.java)

                            val messageReference =
                                database.getReference("Chat").child(lastMessage!!)
                            messageReference.addValueEventListener(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val messageInfo = snapshot.getValue(Chat::class.java)

                                    if (messageInfo != null) {
                                        val chatsToRemove = mutableListOf<ReceiverChatList>()
                                        for (ch in chatList) {
                                            if (ch.userInfo == userInfo) {
                                                chatsToRemove.add(ch)
                                            }
                                        }
                                        chatList.removeAll(chatsToRemove)

                                        val chat = ReceiverChatList(
                                            userInfo!!,
                                            messageInfo!!,
                                            message.timestamp,
                                            isTyping!!
                                        )

                                        chatList.add(chat!!)
                                        getChatList.postValue(chatList)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle error
                                }
                            })
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        chatListReference.addValueEventListener(chatListEventListener!!)
    }
    fun removeChatListEventListener() {
        if (chatListEventListener != null) {
            database.getReference("ChatList").child(currentUser!!)
                .removeEventListener(chatListEventListener!!)
        }
    }


    fun removeFavListEventListener() {
        if (userFavEventListener != null) {
            database.getReference("UserFavourites").child(currentUser!!).removeEventListener(chatListEventListener!!)
        }
    }


    fun setTyping(isTyping:Boolean,receiverId: String){
        val chatReference = database.getReference("ChatList").child(receiverId).child(currentUser!!)
        chatReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    chatReference.child("isTyping").setValue(isTyping)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun addPfp(data: Uri) {
        val storageReference = FirebaseStorage.getInstance().reference.child("User Profile")
        val messageId = database.reference.push().key
        val filePath = storageReference.child("$messageId.png")
        val uploadTask = filePath.putFile(data)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            if (!it.isSuccessful) {
                it.exception?.let {
                    throw it
                }
            }

            return@Continuation filePath.downloadUrl
        })
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val downloadUrl = it.result
                    val url = downloadUrl.toString()

                    val userReference = database.getReference("Users").child(currentUser!!)
                    userReference.child("pfp").setValue(url)

                }
            }
            .addOnFailureListener {
                Log.e("ViewModel", it.message.toString())
            }
    }
}