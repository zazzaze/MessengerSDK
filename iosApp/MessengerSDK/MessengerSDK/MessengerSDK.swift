import Foundation
import SwiftUI
import shared
import SwiftUIRouter

public final class MessengerSDK {
    private let sharedInstance: MessengerSDKMain
    private let configuration: Configuration

    private let simpleChatsListViewModel: ChatsListViewModelImpl
    private let multipleChatsListViewModel: ChatsListViewModelImpl

    public init(configuration: Configuration) {
        KoinFactoryKt.doInitKoin()
        self.configuration = configuration
        sharedInstance = MessengerSDKMain(configuration: .init(apiKey: configuration.apiKey, uid: configuration.userId, token: configuration.token))
        let chatsListStore = ChatListStore()
        self.simpleChatsListViewModel = ChatsListViewModelImpl(store: chatsListStore, multiple: false)
        self.multipleChatsListViewModel =  ChatsListViewModelImpl(store: chatsListStore, multiple: true)
    }

    public func buildMessengerView() -> some View {
        Group {
            if sharedInstance.userScope is MessengerSDKMain.UserScopeMultiple {
                MainView(
                    singleViewModel: self.simpleChatsListViewModel,
                    multipleViewModel: self.multipleChatsListViewModel
                )
            }
            else if let singleScope = sharedInstance.userScope as? MessengerSDKMain.UserScopeSingle {
                let store = ChatStore(chatId: singleScope.chatId)
                Router {
                    ChatView(viewModel: ChatListViewModelImpl(store: store))
                }
            }
            else {
                EmptyView()
            }
        }
    }

    public struct Configuration {
        public let apiKey: String
        public let userId: String
        public let token: String

        public init(apiKey: String, userId: String, token: String) {
            self.apiKey = apiKey
            self.userId = userId
            self.token = token
        }
    }
}
