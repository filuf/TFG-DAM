package com.raj.slotify.services.retrofit.slotifyBackend

import com.raj.slotify.dtos.company.GetCompanyResponse
import com.raj.slotify.dtos.company.GetServicesResponse
import com.raj.slotify.dtos.company.SearchCompaniesResponse
import com.raj.slotify.models.api.PageResponse
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface CompanyService {

    @GET("companies/search")
    suspend fun getCompaniesByQuery(
        @Header("Authorization") authHeader: String,
        @Query("q") q: String,
        @Query("page") page: Int,
    ): Response<PageResponse<SearchCompaniesResponse>>

    @GET("companies/{companyId}")
    suspend fun getCompanyById(
        @Path("companyId") companyId: UUID,
        @Header("Authorization") authHeader: String,
        @Query("fetchMode") fetchMode: String?,
    ): Response<GetCompanyResponse>

    @GET("companies/{companyId}/services")
    suspend fun getServicesByCompanyId(
        @Path("companyId") companyId: UUID,
        @Header("Authorization") authHeader: String,
        @Query("fetchMode") fetchMode: String?,
        @Query("page") page: Int?,
        @Query("sortBy") sortBy: String?,
        @Query("order") order: String?
    ): Response<PageResponse<GetServicesResponse>>

}